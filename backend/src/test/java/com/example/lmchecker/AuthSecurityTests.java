package com.example.lmchecker;

import com.example.lmchecker.dto.LoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class AuthSecurityTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Autowired
    private com.example.lmchecker.repository.SecurityTokenRepository securityTokenRepository;

    @Test
    void testAllFourDemoAccountsCanLogin() throws Exception {
        java.util.Map<String, String> demoAccounts = java.util.Map.of(
            "admin", "Admin@123",
            "regulator", "Regulator@123",
            "reviewer", "Reviewer@123",
            "analyst", "Analyst@123"
        );

        for (java.util.Map.Entry<String, String> entry : demoAccounts.entrySet()) {
            LoginRequest loginRequest = new LoginRequest(entry.getKey(), entry.getValue());
            MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                    .andExpect(jsonPath("$.data.user.username").value(entry.getKey()))
                    .andReturn();

            String token = extractToken(result);
            org.assertj.core.api.Assertions.assertThat(token).isNotBlank();

            System.out.println("BCRYPT_HASH_" + entry.getKey().toUpperCase() + "=" + passwordEncoder.encode(entry.getValue()));
        }
    }

    @Test
    void testLoginSuccessAndRbacAccess() throws Exception {
        // 1. Admin login succeeds and receives JWT
        LoginRequest adminLogin = new LoginRequest("admin", "Admin@123");
        MvcResult adminResult = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminLogin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.user.role").value("ADMIN"))
                .andReturn();

        String adminToken = extractToken(adminResult);

        // 2. Regulator login succeeds
        LoginRequest regLogin = new LoginRequest("regulator", "Regulator@123");
        MvcResult regResult = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(regLogin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.user.role").value("REGULATOR"))
                .andReturn();

        String regulatorToken = extractToken(regResult);

        // 3. Reviewer and Analyst login succeed
        LoginRequest revLogin = new LoginRequest("reviewer", "Reviewer@123");
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(revLogin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.user.role").value("REVIEWER"));

        LoginRequest analystLogin = new LoginRequest("analyst", "Analyst@123");
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(analystLogin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.user.role").value("ANALYST"));

        // 4. Invalid credentials rejected with 401
        LoginRequest badLogin = new LoginRequest("admin", "WrongPassword999");
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(badLogin)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));

        // 5. Unauthenticated request to /api/v1/auth/me returns 401
        mockMvc.perform(get("/api/v1/auth/me"))
                .andExpect(status().isUnauthorized());

        // 6. Authenticated request to /api/v1/auth/me returns current user
        mockMvc.perform(get("/api/v1/auth/me")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("admin"));

        // 7. RBAC Check: /api/v1/users is ADMIN only
        // Admin can access:
        mockMvc.perform(get("/api/v1/users")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());

        // Regulator is denied with 403 FORBIDDEN:
        mockMvc.perform(get("/api/v1/users")
                .header("Authorization", "Bearer " + regulatorToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testRegistrationEmailVerificationAndLockout() throws Exception {
        String testUser = "prod_user_" + System.currentTimeMillis();
        String testEmail = testUser + "@example.com";
        String strongPassword = "StrongPassword@2026!";

        // 1. Weak password rejected
        com.example.lmchecker.dto.RegisterRequest weakPassReq = new com.example.lmchecker.dto.RegisterRequest(
            "Test Subject", testEmail, "weak", "weak", true
        );
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(weakPassReq)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        // 2. Register valid user -> assigns VIEWER role
        com.example.lmchecker.dto.RegisterRequest validReq = new com.example.lmchecker.dto.RegisterRequest(
            "Test Subject", testEmail, strongPassword, strongPassword, true
        );
        MvcResult regRes = mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.role").value("VIEWER"))
                .andExpect(jsonPath("$.data.emailVerified").value(false))
                .andReturn();

        // 3. Duplicate registration rejected
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validReq)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("already")));

        // 4. Verify Email with token from database
        String verificationToken = securityTokenRepository.findByUserEmailAndTokenType(testEmail, com.example.lmchecker.entity.TokenType.EMAIL_VERIFICATION)
                .get(0).getToken();
        org.assertj.core.api.Assertions.assertThat(verificationToken).isNotBlank();

        mockMvc.perform(post("/api/v1/auth/verify-email?token=" + verificationToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.user.emailVerified").value(true));

        // 5. Account lockout after 5 consecutive bad passwords
        for (int i = 0; i < 5; i++) {
            LoginRequest badReq = new LoginRequest(testEmail, "WrongPassword" + i);
            mockMvc.perform(post("/api/v1/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(badReq)))
                    .andExpect(status().isUnauthorized());
        }

        // 6th attempt should be locked
        LoginRequest attemptLocked = new LoginRequest(testEmail, strongPassword);
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(attemptLocked)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("locked")));

        // 6. Forgot password & reset password flow
        mockMvc.perform(post("/api/v1/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new com.example.lmchecker.dto.ForgotPasswordRequest(testEmail))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    private String extractToken(MvcResult result) throws Exception {
        String json = result.getResponse().getContentAsString();
        return objectMapper.readTree(json).path("data").path("accessToken").asText();
    }
}
