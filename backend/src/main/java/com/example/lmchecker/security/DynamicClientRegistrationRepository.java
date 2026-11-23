package com.example.lmchecker.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Component
public class DynamicClientRegistrationRepository implements ClientRegistrationRepository, Iterable<ClientRegistration> {

    private static final Logger log = LoggerFactory.getLogger(DynamicClientRegistrationRepository.class);

    private final ClientRegistration googleRegistration;

    public DynamicClientRegistrationRepository(
            @Value("${app.oauth2.google.client-id:${GOOGLE_CLIENT_ID:${GOOGLE_OAUTH_CLIENT_ID:}}}") String clientId,
            @Value("${app.oauth2.google.client-secret:${GOOGLE_CLIENT_SECRET:${GOOGLE_OAUTH_CLIENT_SECRET:}}}") String clientSecret,
            @Value("${app.oauth2.google.redirect-uri:${GOOGLE_REDIRECT_URI:{baseUrl}/login/oauth2/code/{registrationId}}}") String redirectUri,
            @Value("${app.oauth2.google.client-authentication-method:${GOOGLE_CLIENT_AUTH_METHOD:client_secret_basic}}") String authMethod) {

        String rawClientId = clientId;
        if (!isValidConfigValue(rawClientId)) {
            String fromDotEnv = resolveFromDotEnv("GOOGLE_CLIENT_ID");
            if (isValidConfigValue(fromDotEnv)) {
                rawClientId = fromDotEnv;
            }
        }

        String rawClientSecret = clientSecret;
        if (!isValidConfigValue(rawClientSecret)) {
            String fromDotEnv = resolveFromDotEnv("GOOGLE_CLIENT_SECRET");
            if (isValidConfigValue(fromDotEnv)) {
                rawClientSecret = fromDotEnv;
            }
        }

        String rawRedirectUri = redirectUri;
        if (rawRedirectUri == null || rawRedirectUri.isBlank() || rawRedirectUri.contains("{baseUrl}")) {
            String fromDotEnv = resolveFromDotEnv("GOOGLE_REDIRECT_URI");
            if (fromDotEnv != null && !fromDotEnv.isBlank() && !fromDotEnv.contains("placeholder")) {
                rawRedirectUri = fromDotEnv;
            }
        }

        String cleanClientId = sanitize(rawClientId);
        String cleanClientSecret = sanitize(rawClientSecret);
        String cleanRedirectUri = sanitize(rawRedirectUri);
        if (cleanRedirectUri == null || cleanRedirectUri.isBlank()) {
            cleanRedirectUri = "{baseUrl}/login/oauth2/code/{registrationId}";
        }

        boolean hasValidClientId = isValidConfigValue(cleanClientId);
        boolean hasValidClientSecret = isValidConfigValue(cleanClientSecret);

        if (hasValidClientId && hasValidClientSecret) {
            log.info("Registering Google OAuth 2.0 Client with Client ID: {}***", cleanClientId.substring(0, Math.min(8, cleanClientId.length())));
            ClientRegistration.Builder builder = CommonOAuth2Provider.GOOGLE.getBuilder("google")
                .clientId(cleanClientId)
                .clientSecret(cleanClientSecret)
                .redirectUri(cleanRedirectUri)
                .tokenUri("https://oauth2.googleapis.com/token")
                .scope("openid", "profile", "email");

            if ("client_secret_post".equalsIgnoreCase(authMethod) || "post".equalsIgnoreCase(authMethod)) {
                builder.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST);
                log.info("Google OAuth 2.0 Client configured with client_secret_post authentication method");
            } else {
                builder.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);
            }

            this.googleRegistration = builder.build();
        } else {
            if (hasValidClientId && !hasValidClientSecret) {
                log.warn("Google OAuth Client ID is provided, but Google Client Secret is missing, empty, or a placeholder. " +
                         "Google Sign-In will remain DISABLED to prevent 401 Unauthorized token exchange failures. " +
                         "Please configure a valid GOOGLE_CLIENT_SECRET from Google Cloud Console (APIs & Services > Credentials).");
            } else {
                log.warn("Google OAuth credentials are not configured. Google Sign-In will be disabled until valid GOOGLE_CLIENT_ID and GOOGLE_CLIENT_SECRET are provided in environment or .env file.");
            }
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

    private static String sanitize(String val) {
        if (val == null) return null;
        String trimmed = val.trim();
        if ((trimmed.startsWith("\"") && trimmed.endsWith("\"")) ||
            (trimmed.startsWith("'") && trimmed.endsWith("'"))) {
            if (trimmed.length() >= 2) {
                trimmed = trimmed.substring(1, trimmed.length() - 1).trim();
            }
        }
        return trimmed;
    }

    private static boolean isValidConfigValue(String val) {
        if (val == null) return false;
        String trimmed = sanitize(val);
        if (trimmed == null || trimmed.isBlank()) return false;
        String lower = trimmed.toLowerCase();
        return !lower.contains("placeholder") &&
               !lower.contains("your-google") &&
               !lower.contains("your_google") &&
               !lower.equals("your-google-client-id.apps.googleusercontent.com") &&
               !lower.equals("gocspx-your-google-client-secret") &&
               !lower.equals("gocspx-yourgoogleclientsecret12345");
    }

    private static String resolveFromDotEnv(String key) {
        String[] possiblePaths = { ".env", "backend/.env", "../.env" };
        for (String pathStr : possiblePaths) {
            File file = new File(pathStr);
            if (file.exists() && file.isFile()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        if (line.startsWith("#") || !line.contains("=")) {
                            continue;
                        }
                        int eqIdx = line.indexOf('=');
                        String k = line.substring(0, eqIdx).trim();
                        if (k.equals(key)) {
                            return sanitize(line.substring(eqIdx + 1).trim());
                        }
                    }
                } catch (Exception ignored) {
                }
            }
        }
        return null;
    }
}
