package com.example.lmchecker.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private static final Logger log = LoggerFactory.getLogger(OAuth2AuthenticationFailureHandler.class);

    private final String frontendLoginUrl;
    private final HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository;

    public OAuth2AuthenticationFailureHandler(
            @Value("${app.oauth2.login-url:http://localhost:3000/login}") String frontendLoginUrl,
            HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository) {
        this.frontendLoginUrl = frontendLoginUrl;
        this.cookieAuthorizationRequestRepository = cookieAuthorizationRequestRepository;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        log.error("OAuth2 authentication failure: {}", exception.getMessage());

        Throwable cause = exception;
        while (cause.getCause() != null && cause.getCause() != cause) {
            cause = cause.getCause();
        }

        log.error("OAuth2 ROOT CAUSE: {}: {}", cause.getClass().getName(), cause.getMessage());

        String userFriendlyMessage = "Google authentication failed";
        String exMsg = exception != null ? exception.getMessage() : "";
        if (exMsg != null && exMsg.contains("401 Unauthorized")) {
            userFriendlyMessage = "Google authentication failed: 401 Unauthorized. The configured Google Client Secret is invalid, expired, or mismatched with Client ID in Google Cloud Console.";
            log.error("CRITICAL OAUTH CONFIGURATION ERROR: Google rejected the OAuth 2.0 token exchange with 401 Unauthorized. Verify GOOGLE_CLIENT_ID and GOOGLE_CLIENT_SECRET in Google Cloud Console (APIs & Services > Credentials). Ensure the Client Secret is actively enabled and matches the Web Application Client ID.");
        } else if (exMsg != null && !exMsg.isBlank()) {
            userFriendlyMessage = "Google authentication failed: " + exMsg;
        }

        cookieAuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);

        String targetUrl = UriComponentsBuilder.fromUriString(frontendLoginUrl)
            .queryParam("error", userFriendlyMessage)
            .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
