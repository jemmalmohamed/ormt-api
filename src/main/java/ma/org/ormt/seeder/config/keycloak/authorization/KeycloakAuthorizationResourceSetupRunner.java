package ma.org.ormt.seeder.config.keycloak.authorization;

import java.util.Arrays;
import java.util.List;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ma.org.ormt.security.keycloak.dto.request.KeycloakResourceRequestDto;
import ma.org.ormt.security.keycloak.services.KeycloakConnectService;
import ma.org.ormt.security.keycloak.services.client.KeycloakClientService;
import ma.org.ormt.security.keycloak.services.realm.KeycloakRealmService;
import ma.org.ormt.seeder.config.keycloak.services.KeycloakStarterHelperService;

@Log4j2
@Configuration
@RequiredArgsConstructor
@Order(3)
public class KeycloakAuthorizationResourceSetupRunner implements CommandLineRunner {

    private final KeycloakRealmService keycloakRealmService;
    private final KeycloakStarterHelperService keycloakStarterHelperService;
    private final KeycloakClientService keycloakClientService;
    private final KeycloakConnectService keycloakService;

    @Value("${keycloak.realm}")
    private String realmName;

    @Value("${keycloak.clients.backend.id}")
    private String backendClientName;

    @Value("${starter.keycloak.seed}")
    private boolean seedKeycloak;

    // Resource names split for seeding
    private static final List<String> RESOURCES_GROUP_1 = Arrays.asList(
            "domaine", "espace", "chiffrecle", "indicateur", "dimension", "partenaire");
    private static final List<String> RESOURCES_GROUP_2 = Arrays.asList(
            "user", "region", "province", "role", "datasource", "publication", "tableaubord");

    @Override
    public void run(String... args) {
        if (!seedKeycloak) {
            log.info("### KEYCLOAK: Skipping keycloak resource seeding");
            return;
        }
        log.info("### KEYCLOAK: Starting resource seeding...");
        setupKeycloakResources(RESOURCES_GROUP_1);
        setupKeycloakResources(RESOURCES_GROUP_2);
    }

    private void setupKeycloakResources(List<String> resources) {
        log.info("### KEYCLOAK: Setting up resources: {} ###", resources);

        Keycloak keycloak = null;
        try {
            keycloak = keycloakService.getKeyCloakAdminCli();
            ClientResource backendClientResource = keycloakRealmService
                    .getClientResource(keycloak, realmName, backendClientName)
                    .orElse(null);

            if (backendClientResource == null) {
                log.warn("### KEYCLOAK: Backend client resource not found for realm '{}' and client '{}'", realmName,
                        backendClientName);
                return;
            }

            keycloakClientService.enableClientAuthorization(backendClientResource);
            List<KeycloakResourceRequestDto> resourcesJson = keycloakStarterHelperService.loadResourceDtos(resources);

            resourcesJson.forEach(resource -> setupResource(backendClientResource, resource));

            setupPoliciesAndPermissions(resourcesJson);

        } catch (Exception e) {
            log.error("### KEYCLOAK: Error during resource setup", e);
        } finally {
            if (keycloak != null) {
                try {
                    keycloak.tokenManager().logout();
                    keycloak.close();
                } catch (Exception ex) {
                    log.warn("### KEYCLOAK: Error closing Keycloak client", ex);
                }
            }
        }
        log.info("### KEYCLOAK: Resources setup done for {}", resources);
    }

    private void setupResource(ClientResource clientResource, KeycloakResourceRequestDto resource) {
        try {
            keycloakStarterHelperService.createOrUpdateResource(clientResource, resource);
        } catch (Exception e) {
            log.error("### KEYCLOAK: Failed to setup resource '{}'", resource.getName(), e);
        }
    }

    private void setupPoliciesAndPermissions(List<KeycloakResourceRequestDto> resources) {
        try {
            keycloakStarterHelperService.createPoliciesAndPermissionList(realmName, backendClientName, resources);
        } catch (Exception e) {
            log.error("### KEYCLOAK: Failed to setup policies and permissions", e);
        }
    }
}
