package com.example.lmchecker;

import com.example.lmchecker.security.DynamicClientRegistrationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

import static org.assertj.core.api.Assertions.assertThat;

class DynamicClientRegistrationRepositoryTest {

    @Test
    @DisplayName("Valid Client ID and Secret registers Google Client successfully")
    void testValidCredentialsRegisterGoogle() {
        DynamicClientRegistrationRepository repo = new DynamicClientRegistrationRepository(
            "123456789-testclient.apps.googleusercontent.com",
            "GOCSPX-genuineSecretValue123456",
            "http://localhost:8080/login/oauth2/code/google",
            "client_secret_basic"
        );

        assertThat(repo.isGoogleConfigured()).isTrue();
        ClientRegistration reg = repo.findByRegistrationId("google");
        assertThat(reg).isNotNull();
        assertThat(reg.getClientId()).isEqualTo("123456789-testclient.apps.googleusercontent.com");
        assertThat(reg.getClientSecret()).isEqualTo("GOCSPX-genuineSecretValue123456");
        assertThat(reg.getClientAuthenticationMethod()).isEqualTo(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);
        assertThat(reg.getProviderDetails().getTokenUri()).isEqualTo("https://oauth2.googleapis.com/token");
    }

    @Test
    @DisplayName("Missing or placeholder Client Secret leaves Google disabled to prevent 401 token exchange")
    void testPlaceholderOrEmptySecretDisabled() {
        // Empty secret
        DynamicClientRegistrationRepository repoEmpty = new DynamicClientRegistrationRepository(
            "123456789-testclient.apps.googleusercontent.com",
            "",
            "http://localhost:8080/login/oauth2/code/google",
            "client_secret_basic"
        );
        assertThat(repoEmpty.isGoogleConfigured()).isFalse();
        assertThat(repoEmpty.findByRegistrationId("google")).isNull();

        // Placeholder secret
        DynamicClientRegistrationRepository repoPlaceholder = new DynamicClientRegistrationRepository(
            "123456789-testclient.apps.googleusercontent.com",
            "GOCSPX-your-google-client-secret",
            "http://localhost:8080/login/oauth2/code/google",
            "client_secret_basic"
        );
        assertThat(repoPlaceholder.isGoogleConfigured()).isFalse();

        // Null secret
        DynamicClientRegistrationRepository repoNull = new DynamicClientRegistrationRepository(
            "123456789-testclient.apps.googleusercontent.com",
            null,
            "http://localhost:8080/login/oauth2/code/google",
            "client_secret_basic"
        );
        assertThat(repoNull.isGoogleConfigured()).isFalse();
    }

    @Test
    @DisplayName("Quotes and whitespace in credentials are sanitized cleanly")
    void testSanitizationOfQuotesAndWhitespace() {
        DynamicClientRegistrationRepository repo = new DynamicClientRegistrationRepository(
            "  \"987654321-quoted.apps.googleusercontent.com\"  ",
            " 'GOCSPX-singleQuotedSecret12345' ",
            " \"http://localhost:8080/login/oauth2/code/google\" ",
            "client_secret_post"
        );

        assertThat(repo.isGoogleConfigured()).isTrue();
        ClientRegistration reg = repo.findByRegistrationId("google");
        assertThat(reg).isNotNull();
        assertThat(reg.getClientId()).isEqualTo("987654321-quoted.apps.googleusercontent.com");
        assertThat(reg.getClientSecret()).isEqualTo("GOCSPX-singleQuotedSecret12345");
        assertThat(reg.getClientAuthenticationMethod()).isEqualTo(ClientAuthenticationMethod.CLIENT_SECRET_POST);
    }
}
