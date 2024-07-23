package ma.org.ormt.security.keycloak.config;

import java.util.Collections;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.authorization.client.AuthzClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ma.org.ormt.core.exceptions.handlers.KeycloakException;
import ma.org.ormt.security.keycloak.services.client.KeycloakClientService;
import ma.org.ormt.security.keycloak.services.realm.KeycloakRealmService;

@Log4j2
@Service
@RequiredArgsConstructor
public class KeycloakAuthzService {

    private final KeycloakService keycloakService;

    private final KeycloakRealmService keycloakRealmService;

    private final KeycloakClientService keycloakClientService;

    @Value("${keycloak.auth-server-url}")
    private String keycloakServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.clients.backend.id}")
    private String clientId;

    @Value("${keycloak.admin.username}")
    private String adminUser;

    @Value("${keycloak.admin.password}")
    private String adminPassword;

    public AuthzClient createAuthzClient() {
        Keycloak keycloak = keycloakService.getKeyCloakAdminCli();
        RealmResource realmResource = keycloakRealmService.getRealmResource(keycloak, realm);
        String secret = keycloakClientService.getClientSecret(realmResource,
                clientId);
        log.info("### KEYCLOAK: Secret: " + secret);
        try {
            AuthzClient authzClient = AuthzClient.create(new org.keycloak.authorization.client.Configuration(
                    "http://ormt-kc:8080", // Keycloak server URL
                    "ormt", // The realm where the client is located
                    "ormt-api", // The client Name
                    Collections.singletonMap("secret", "SSnam21521xFoFQAqi5TA68jKV3nBOyl"), // The client credentials
                    null // HttpClient instance
            // AuthzClient authzClient = AuthzClient.create(new
            // org.keycloak.authorization.client.Configuration(
            // keycloakServerUrl, // Keycloak server URL
            // realm, // The realm where the client is located
            // clientId, // The client Name
            // Collections.singletonMap("secret", secret), // The client credentials
            // null // HttpClient instance
            ));

            log.info("### KEYCLOAK: AuthzClient created successfully");

            return authzClient;
        } catch (Exception e) {
            log.error("Error while creating AuthzClient", e);
            throw new KeycloakException("Error creating AuthzClient");
        }
    }

}
