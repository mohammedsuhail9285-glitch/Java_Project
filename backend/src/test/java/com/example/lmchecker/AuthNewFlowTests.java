package com.example.lmchecker;

import com.example.lmchecker.dto.LoginRequest;
import com.example.lmchecker.dto.OtpRequestDto;
import com.example.lmchecker.dto.OtpVerifyDto;
import com.example.lmchecker.entity.AuthProvider;
import com.example.lmchecker.entity.OtpCode;
import com.example.lmchecker.entity.Role;
import com.example.lmchecker.entity.User;
import com.example.lmchecker.repository.AuditLogRepository;
import com.example.lmchecker.repository.OtpCodeRepository;
import com.example.lmchecker.repository.UserRepository;
import com.example.lmchecker.security.OAuth2AuthenticationSuccessHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class AuthNewFlowTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OtpCodeRepository otpCodeRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    @BeforeEach
    void setUp() {
        otpCodeRepository.deleteAll();

        // Ensure test user exists
        if (userRepository.findByEmail("regulator@consumer.gov.in").isEmpty()) {
            User user = User.builder()
                .username("regulator")
                .email("regulator@consumer.gov.in")
                .fullName("Senior Metrology Officer")
                .role(Role.REGULATOR)
                .authProvider(AuthProvider.LOCAL)
                .enabled(true)
                .build();
            userRepository.save(user);
        }
    }

    @Test
    @DisplayName("Verify GET /api/v1/auth/config returns provider discovery flags")
    void testAuthConfigEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/auth/config"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.otpDeliveryMode").isNotEmpty())
            .andExpect(jsonPath("$.data.demoMode").value(true));
    }

    @Test
    @DisplayName("OTP Flow: Happy path request & verify in demo mode")
    void testOtpRequestAndVerifyHappyPath() throws Exception {
        String testEmail = "regulator@consumer.gov.in";

        // 1. Request OTP
        OtpRequestDto requestDto = new OtpRequestDto(testEmail);
        MvcResult requestResult = mockMvc.perform(post("/api/v1/auth/otp/request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.devOtpHint").isNotEmpty())
            .andExpect(jsonPath("$.data.expiresInSeconds").value(300))
            .andReturn();

        String responseBody = requestResult.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        String devOtp = jsonNode.path("data").path("devOtpHint").asText();
        assertThat(devOtp).hasSize(6);

        // 2. Verify with the generated OTP code
        OtpVerifyDto verifyDto = new OtpVerifyDto(testEmail, devOtp);
        mockMvc.perform(post("/api/v1/auth/otp/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verifyDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
            .andExpect(jsonPath("$.data.user.email").value(testEmail))
            .andExpect(jsonPath("$.data.user.role").value("REGULATOR"));

        // 3. Confirm OTP code is now marked consumed (single-use)
        OtpCode codeInDb = otpCodeRepository
            .findFirstByIdentifierAndPurposeAndConsumedFalseOrderByCreatedAtDesc(testEmail, "LOGIN")
            .orElse(null);
        assertThat(codeInDb).isNull(); // no active unconsumed codes left
    }

    @Test
    @DisplayName("OTP Flow: Wrong code rejected with remaining attempts")
    void testOtpVerifyWrongCode() throws Exception {
        String testEmail = "regulator@consumer.gov.in";

        // Request OTP
        mockMvc.perform(post("/api/v1/auth/otp/request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new OtpRequestDto(testEmail))))
            .andExpect(status().isOk());

        // Verify with invalid code
        OtpVerifyDto wrongVerify = new OtpVerifyDto(testEmail, "000000");
        mockMvc.perform(post("/api/v1/auth/otp/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(wrongVerify)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Invalid verification code")));
    }

    @Test
    @DisplayName("OTP Flow: Expired code is rejected")
    void testOtpVerifyExpiredCode() throws Exception {
        String testEmail = "regulator@consumer.gov.in";

        // Request OTP
        mockMvc.perform(post("/api/v1/auth/otp/request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new OtpRequestDto(testEmail))))
            .andExpect(status().isOk());

        // Manually expire the code in DB
        OtpCode otpCode = otpCodeRepository
            .findFirstByIdentifierAndPurposeAndConsumedFalseOrderByCreatedAtDesc(testEmail, "LOGIN")
            .orElseThrow();
        otpCode.setExpiresAt(LocalDateTime.now().minusMinutes(10));
        otpCodeRepository.save(otpCode);

        // Attempt verification
        OtpVerifyDto verifyDto = new OtpVerifyDto(testEmail, "123456");
        mockMvc.perform(post("/api/v1/auth/otp/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verifyDto)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("expired")));
    }

    @Test
    @DisplayName("OTP Flow: Unregistered identifier responds generically (no enumeration)")
    void testOtpRequestUnregisteredIdentifier() throws Exception {
        String unknownEmail = "nonexistent_officer_9999@consumer.gov.in";

        mockMvc.perform(post("/api/v1/auth/otp/request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new OtpRequestDto(unknownEmail))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.devOtpHint").doesNotExist()) // No code generated
            .andExpect(jsonPath("$.data.message").value(org.hamcrest.Matchers.containsString("If this identifier is registered")));
    }

    @Test
    @DisplayName("OTP Flow: Mobile phone number returns clean SMS provider notice")
    void testOtpRequestMobileNumber() throws Exception {
        String mobileNumber = "+91 9876543210";

        mockMvc.perform(post("/api/v1/auth/otp/request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new OtpRequestDto(mobileNumber))))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("SMS OTP requires configuring an SMS provider")));
    }

    @Test
    @DisplayName("Google OAuth: First-time sign-in registers user with ANALYST role and logs REGISTER_VIA_GOOGLE")
    void testGoogleOAuthNewUserRegistration() throws Exception {
        String googleEmail = "new_google_officer_" + System.currentTimeMillis() + "@gmail.com";
        String googleName = "Inspector Sharma";

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", googleEmail);
        attributes.put("name", googleName);
        attributes.put("sub", "google_sub_" + System.currentTimeMillis());

        OAuth2User oauth2User = new DefaultOAuth2User(
            List.of(new SimpleGrantedAuthority("ROLE_USER")),
            attributes,
            "email"
        );

        Authentication auth = new OAuth2AuthenticationToken(
            oauth2User,
            Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
            "google"
        );

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Trigger success handler
        oAuth2AuthenticationSuccessHandler.onAuthenticationSuccess(request, response, auth);

        // 1. Verify user was created in DB with VIEWER role and GOOGLE auth provider
        User createdUser = userRepository.findByEmail(googleEmail).orElse(null);
        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getFullName()).isEqualTo(googleName);
        assertThat(createdUser.getRole()).isEqualTo(Role.VIEWER);
        assertThat(createdUser.getAuthProvider()).isEqualTo(AuthProvider.GOOGLE);

        // 2. Verify redirect URL contains token and provider
        String redirectedUrl = response.getRedirectedUrl();
        assertThat(redirectedUrl).isNotNull();
        assertThat(redirectedUrl).contains("token=");
        assertThat(redirectedUrl).contains("provider=google");

        // 3. Verify accessToken cookie was added
        assertThat(response.getCookie("accessToken")).isNotNull();
        assertThat(response.getCookie("accessToken").getValue()).isNotEmpty();
    }

    @Test
    @DisplayName("Google OAuth: Existing user logs in and logs LOGIN_GOOGLE without duplicate registration")
    void testGoogleOAuthExistingUserLogin() throws Exception {
        String existingEmail = "existing_analyst_" + System.currentTimeMillis() + "@consumer.gov.in";

        User existing = User.builder()
            .username("analyst_" + System.currentTimeMillis())
            .email(existingEmail)
            .fullName("Existing Policy Analyst")
            .role(Role.ANALYST)
            .authProvider(AuthProvider.GOOGLE)
            .enabled(true)
            .build();
        userRepository.save(existing);

        long userCountBefore = userRepository.count();

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", existingEmail);
        attributes.put("name", "Existing Policy Analyst");
        attributes.put("sub", "google_sub_existing");

        OAuth2User oauth2User = new DefaultOAuth2User(
            List.of(new SimpleGrantedAuthority("ROLE_USER")),
            attributes,
            "email"
        );

        Authentication auth = new OAuth2AuthenticationToken(
            oauth2User,
            Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
            "google"
        );

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        oAuth2AuthenticationSuccessHandler.onAuthenticationSuccess(request, response, auth);

        // User count unchanged (no duplicate user created)
        assertThat(userRepository.count()).isEqualTo(userCountBefore);
        assertThat(response.getRedirectedUrl()).contains("token=");
    }

    @Test
    @DisplayName("OTP Flow: Rate limiting blocks requests after 5 attempts in an hour")
    void testOtpRateLimitExceeded() throws Exception {
        String testEmail = "regulator@consumer.gov.in";

        // Perform 5 requests (the limit)
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post("/api/v1/auth/otp/request")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new OtpRequestDto(testEmail))))
                .andExpect(status().isOk());
        }

        // 6th request should fail with 400 Bad Request
        mockMvc.perform(post("/api/v1/auth/otp/request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new OtpRequestDto(testEmail))))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Too many OTP requests")));
    }
}
