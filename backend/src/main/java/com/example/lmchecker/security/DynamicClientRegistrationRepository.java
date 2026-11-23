package com.example.lmchecker.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Component
public class DynamicClientRegistrationRepository implements ClientRegistrationRepository, Iterable<ClientRegistration> {

    private static final Logger log = LoggerFactory.getLogger(DynamicClientRegistrationRepository.class);

    private final ClientRegistration googleRegistration;

    public DynamicClientRegistrationRepository(
            @Value("${GOOGLE_CLIENT_ID:${GOOGLE_OAUTH_CLIENT_ID:${app.oauth2.google.client-id:}}}") String clientId,
            @Value("${GOOGLE_CLIENT_SECRET:${GOOGLE_OAUTH_CLIENT_SECRET:${app.oauth2.google.client-secret:}}}") String clientSecret,
            @Value("${GOOGLE_REDIRECT_URI:${GOOGLE_OAUTH_REDIRECT_URI:{baseUrl}/login/oauth2/code/{registrationId}}}") String redirectUri) {

        if (clientId != null && !clientId.trim().isEmpty() && !clientId.contains("placeholder") && !clientId.equals("YOUR_GOOGLE_CLIENT_ID")) {
            log.info("Registering Google OAuth 2.0 Client with Client ID: {}***", clientId.substring(0, Math.min(8, clientId.length())));
            this.googleRegistration = CommonOAuth2Provider.GOOGLE.getBuilder("google")
                .clientId(clientId.trim())
                .clientSecret(clientSecret != null ? clientSecret.trim() : "")
                .redirectUri(redirectUri)
                .scope("openid", "profile", "email")
                .build();
        } else {
            log.warn("Google OAuth Client ID is not configured. Google Sign-In will be disabled until GOOGLE_CLIENT_ID is provided.");
            this.googleRegistration = null;
        }
    }

    @Override
    public ClientRegistration findByRegistrationId(String registrationId) {
        if ("google".equalsIgnoreCase(registrationId)) {
            return googleRegistration;
        }
        return null;
    }

    @Override
    public Iterator<ClientRegistration> iterator() {
        return googleRegistration != null ? List.of(googleRegistration).iterator() : Collections.emptyIterator();
    }

    public boolean isGoogleConfigured() {
        return googleRegistration != null;
    }
}
