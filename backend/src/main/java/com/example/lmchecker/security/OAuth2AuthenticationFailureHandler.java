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

    public OAuth2AuthenticationFailureHandler(
            @Value("${app.oauth2.login-url:http://localhost:3000/login}") String frontendLoginUrl) {
        this.frontendLoginUrl = frontendLoginUrl;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        log.warn("OAuth2 authentication failure: {}", exception.getMessage());

        String targetUrl = UriComponentsBuilder.fromUriString(frontendLoginUrl)
            .queryParam("error", "Google authentication failed: " + exception.getMessage())
            .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
