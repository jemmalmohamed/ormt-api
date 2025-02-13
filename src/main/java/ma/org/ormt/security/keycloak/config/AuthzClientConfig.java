package ma.org.ormt.security.keycloak.config;

import java.util.Collections;

import org.keycloak.authorization.client.AuthzClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Configuration
public class AuthzClientConfig {

    @Value("${keycloak.auth-server-url}")
    private String keycloakServerUrl;

    @Value("${keycloak.admin.realm}")
    private String adminRealm;
    @Value("${keycloak.admin.client}")
    private String adminClient;

    @Value("${keycloak.admin.username}")
    private String adminUser;

    @Value("${keycloak.admin.password}")
    private String adminPassword;

    @Value("${keycloak.secret}")
    private String secret;

    @Bean
    public AuthzClient authzClient() {

        AuthzClient authzClient = AuthzClient.create(new org.keycloak.authorization.client.Configuration(
                keycloakServerUrl, // Keycloak server URL
                "ormt", // The realm where the client is located
                "ormt-api", // The client ID
                Collections.singletonMap("secret",
                        "SSnam21521xFoFQAqi5TA68jKV3nBOyl"), // The client credentials
                null // HttpClient instance
        ));

        try {
            // AccessTokenResponse response = authzClient.obtainAccessToken();
            authzClient.obtainAccessToken();

        } catch (Exception e) {
            log.error("Error while creating authzClient", e);
        }
        return authzClient;
    }
}
