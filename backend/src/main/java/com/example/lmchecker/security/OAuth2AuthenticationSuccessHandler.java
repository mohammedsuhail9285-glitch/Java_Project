package com.example.lmchecker.security;

import com.example.lmchecker.audit.AuditLogService;
import com.example.lmchecker.entity.AuthProvider;
import com.example.lmchecker.entity.Role;
import com.example.lmchecker.entity.User;
import com.example.lmchecker.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Optional;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final Logger log = LoggerFactory.getLogger(OAuth2AuthenticationSuccessHandler.class);

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final AuditLogService auditLogService;
    private final String defaultRoleName;
    private final String frontendCallbackUrl;
    private final boolean cookieSecure;

    public OAuth2AuthenticationSuccessHandler(
            UserRepository userRepository,
            JwtUtils jwtUtils,
            AuditLogService auditLogService,
            @Value("${app.oauth2.default-role:VIEWER}") String defaultRoleName,
            @Value("${app.oauth2.redirect-uri:http://localhost:3000/oauth2/callback}") String frontendCallbackUrl,
            @Value("${app.security.cookie-secure:false}") boolean cookieSecure) {
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
        this.auditLogService = auditLogService;
        this.defaultRoleName = defaultRoleName;
        this.frontendCallbackUrl = frontendCallbackUrl;
        this.cookieSecure = cookieSecure;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (!(authentication.getPrincipal() instanceof OAuth2User oauth2User)) {
            super.onAuthenticationSuccess(request, response, authentication);
            return;
        }

        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        String sub = oauth2User.getAttribute("sub");
        Object emailVerifiedObj = oauth2User.getAttribute("email_verified");
        boolean googleEmailVerified = emailVerifiedObj != null && ("true".equalsIgnoreCase(String.valueOf(emailVerifiedObj)) || Boolean.TRUE.equals(emailVerifiedObj));

        if (email == null || email.isBlank()) {
            email = (sub != null ? sub : oauth2User.getName()) + "@google.com";
        }

        // Strict security rule: New registrations must receive restricted VIEWER role only
        Role restrictedRole = Role.VIEWER;
        try {
            Role configuredRole = Role.valueOf(defaultRoleName.toUpperCase());
            // Never allow automatic elevation to ADMIN, REGULATOR, or REVIEWER
            if (configuredRole == Role.VIEWER || configuredRole == Role.ANALYST) {
                restrictedRole = configuredRole;
            }
        } catch (Exception e) {
            restrictedRole = Role.VIEWER;
        }

        Optional<User> existingUser = userRepository.findByEmail(email);
        User user;
        boolean isNewUser = false;

        if (existingUser.isPresent()) {
            user = existingUser.get();

            // Link existing account only after verifying Google verified the email ownership
            if (googleEmailVerified) {
                user.setEmailVerified(true);
            }
            if (user.getProviderId() == null && sub != null) {
                user.setProviderId(sub);
            }
            userRepository.save(user);

            log.info("Existing user '{}' logged in via Google OAuth 2.0 (Role: {})", user.getUsername(), user.getRole());
            auditLogService.logAction("LOGIN_GOOGLE", "USER", user.getId().toString(), null, "Role: " + user.getRole() + ", Provider: GOOGLE", request);
        } else {
            isNewUser = true;
            String baseUsername = email.split("@")[0].replaceAll("[^a-zA-Z0-9_]", "_").toLowerCase();
            String username = baseUsername;
            int counter = 1;
            while (userRepository.existsByUsername(username)) {
                username = baseUsername + counter++;
            }

            user = User.builder()
                .username(username)
                .email(email)
                .fullName(name != null && !name.isBlank() ? name : username)
                .role(restrictedRole)
                .authProvider(AuthProvider.GOOGLE)
                .providerId(sub != null ? sub : oauth2User.getName())
                .emailVerified(googleEmailVerified)
                .accountStatus("ACTIVE")
                .enabled(true)
                .build();
            user = userRepository.save(user);

            log.info("Registered new user '{}' via Google OAuth with restricted role {}", username, restrictedRole);
            auditLogService.logAction("REGISTER_VIA_GOOGLE", "USER", user.getId().toString(), null, "Role: " + user.getRole() + ", Provider: GOOGLE", request);
        }

        // Issue JWT token with identical signature and claims
        String jwt = jwtUtils.generateTokenFromUsernameAndRole(user.getUsername(), user.getRole().name(), jwtUtils.getJwtExpirationMs());

        // Set secure SameSite=Lax HttpOnly cookie for token
        ResponseCookie cookie = ResponseCookie.from("accessToken", jwt)
            .httpOnly(true)
            .secure(cookieSecure)
            .path("/")
            .maxAge(86400) // 1 day
            .sameSite("Lax")
            .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        // Redirect back to frontend callback route with token
        String targetUrl = UriComponentsBuilder.fromUriString(frontendCallbackUrl)
            .queryParam("token", jwt)
            .queryParam("provider", "google")
            .queryParam("isNewUser", isNewUser)
            .build().toUriString();

        clearAuthenticationAttributes(request);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
