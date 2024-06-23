package ma.org.ancfcc.pva.seeder.config.keycloak.authorization;

import java.util.ArrayList;
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
import ma.org.ancfcc.pva.security.keycloak.config.KeycloakService;
import ma.org.ancfcc.pva.security.keycloak.representation.ResourceJsonRepresentation;
import ma.org.ancfcc.pva.security.keycloak.services.authorization.resource.KeycloakResourceService;
import ma.org.ancfcc.pva.security.keycloak.services.client.KeycloakClientService;
import ma.org.ancfcc.pva.security.keycloak.services.realm.KeycloakRealmService;
import ma.org.ancfcc.pva.seeder.config.keycloak.services.KeycloakStarterService;

@Log4j2
@Configuration
@RequiredArgsConstructor
@Order(3)
public class KeycloakAuthorizationResourceSetupRunner implements CommandLineRunner {

    private final KeycloakRealmService keycloakRealmService;

    private final KeycloakStarterService keycloakStarterService;

    private final KeycloakClientService keycloakClientService;

    private final KeycloakService keycloakService;

    private final KeycloakResourceService keycloakResourceService;

    @Value("${keycloak.realm}")
    private String realmName;

    @Value("${keycloak.clients.backend.id}")
    private String backendClientName;

    @Value("${starter.keycloak.seed}")
    private boolean seedKeycloak;

    @Override
    public void run(String... args) throws Exception {

        if (!seedKeycloak) {
            log.info("### KEYCLOAK: Skipping keycloak resource seeding");
            return;
        }
        // split the resources into two lists and call the setupKeycloakResources method
        // twice
        // to void 10 resources in one list
        List<String> resources1 = Arrays.asList(

                "capteur",
                "avion",
                "organisme",
                "planaction",
                "mission",
                "objet",
                "user",
                "basemap",
                "carte"

        );

        List<String> resources2 = Arrays.asList(
        // "mission",
        // "auth",
        // "organisme",
        // "planaction",
        // "avion"

        );

        setupKeycloakResources(resources1);
        setupKeycloakResources(resources2);

    }

    private void setupKeycloakResources(List<String> resources) {

        log.info("### KEYCLOAK: Setting up keycloak resources ... ###");

        Keycloak keycloak = keycloakService.getKeyCloakAdminCli();

        ClientResource backendClientResource = keycloakRealmService
                .getClientResource(keycloak, realmName, backendClientName)
                .orElse(null);

        if (backendClientResource != null) {
            keycloakClientService.enableClientAuthorization(backendClientResource);
            List<ResourceJsonRepresentation> resourcesJson = getResourceJsonFile(resources);

            resourcesJson.forEach(resource -> setupResource(backendClientResource, resource));

            keycloak.tokenManager().logout();
            keycloak.close();

            setupPoliciesAndPermissions(resourcesJson);

        }

        log.info("### KEYCLOAK: resources setup done");

    }

    private List<ResourceJsonRepresentation> getResourceJsonFile(List<String> resources) {
        List<ResourceJsonRepresentation> resourcesJson = new ArrayList<>();
        for (String resourceName : resources) {
            resourcesJson.addAll(keycloakResourceService.getJsonResourceRepresentations(resourceName));
        }
        return resourcesJson;
    }

    private void setupResource(ClientResource clientResource, ResourceJsonRepresentation resource) {
        keycloakStarterService.createOrUpdateResource(clientResource, resource);
    }

    private void setupPoliciesAndPermissions(List<ResourceJsonRepresentation> resources) {

        keycloakStarterService.createPoliciesAndPermissionList(realmName, backendClientName, resources);
    }

}
