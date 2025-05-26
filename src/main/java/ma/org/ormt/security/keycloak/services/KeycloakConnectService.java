package ma.org.ormt.security.keycloak.services;

import java.util.Collections;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.authorization.client.AuthzClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
public class KeycloakConnectService {

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

    public Keycloak getKeyCloakAdminCli() {
        return KeycloakBuilder.builder()
                .serverUrl(keycloakServerUrl)
                .realm(adminRealm)
                .clientId(adminClient)
                .grantType(OAuth2Constants.PASSWORD)
                .username(adminUser)
                .password(adminPassword)
                .build();
    }

    public AuthzClient getAuthzClient() {
        AuthzClient authzClient = AuthzClient.create(new org.keycloak.authorization.client.Configuration(
                keycloakServerUrl,
                "ormt",
                "ormt-api",
                Collections.singletonMap("secret", secret),
                null));

        try {
            authzClient.obtainAccessToken();
        } catch (Exception e) {
            log.error("Error while creating authzClient", e);
        }
        return authzClient;
    }

}
