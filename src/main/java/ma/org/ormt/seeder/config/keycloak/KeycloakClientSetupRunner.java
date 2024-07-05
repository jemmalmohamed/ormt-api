package ma.org.ormt.seeder.config.keycloak;

import java.util.Arrays;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ma.org.ormt.security.keycloak.config.KeycloakService;
import ma.org.ormt.security.keycloak.services.client.KeycloakClientService;
import ma.org.ormt.security.keycloak.services.realm.KeycloakRealmService;

@Log4j2
@Configuration
@RequiredArgsConstructor
@Order(1)
public class KeycloakClientSetupRunner implements CommandLineRunner {

    private final KeycloakRealmService keycloakRealmService;
    private final KeycloakService keycloakService;
    private final KeycloakClientService keycloakClientService;

    @Value("${keycloak.secret}")
    private String kcSecret;

    @Value("${keycloak.realm}")
    private String kcRealmName;

    @Value("${keycloak.clients.backend.id}")
    private String kcBackendClientId;

    @Value("${keycloak.clients.backend.name}")
    private String kcBackendClientName;

    @Value("${keycloak.clients.backend.root-url}")
    private String kcOrmtApiRootUrl;

    @Value("${keycloak.clients.frontend.id}")
    private String kcFrontendClientId;

    @Value("${keycloak.clients.frontend.name}")
    private String kcFrontendClientName;

    @Value("${keycloak.clients.frontend.root-url}")
    private String kcFrontendRootUrl;

    @Value("${starter.keycloak.reset}")
    private boolean kcResetKeycloak;

    @Value("${starter.keycloak.seed}")
    private boolean kcSeedKeycloak;

    @Override
    public void run(String... args) throws Exception {

        Keycloak keycloak = keycloakService.getKeyCloakAdminCli();

        if (kcResetKeycloak) {
            this.keycloakRealmService.deleteRealm(keycloak, kcRealmName);
        }

        if (!kcSeedKeycloak) {
            log.info("### KEYCLOAK: Skipping keycloak client seeding");
            return;
        }

        log.info("### KEYCLOAK: Setting Realm and Client ... ###");

        setupMasterRealm(keycloak, "master");

        RealmResource realmResource = setupRealm(keycloak, kcRealmName);

        setupBackendClient(realmResource, kcBackendClientId, kcBackendClientName, kcOrmtApiRootUrl, kcSecret);

        setupFrontendClient(realmResource, kcFrontendClientId, kcFrontendClientName, kcFrontendRootUrl, kcSecret);

        keycloak.tokenManager().logout();
        keycloak.close();

        log.info("### KEYCLOAK: Realm and Client Done ... ###");
    }

    private void setupMasterRealm(Keycloak keycloak, String masterRealm) {
        log.info("### KEYCLOAK: Setting master realm   ... ###");
        RealmResource masterRealmResource = keycloakRealmService.getRealmResource(keycloak, masterRealm);
        RealmRepresentation masterRealmRepresentation = masterRealmResource.toRepresentation();
        masterRealmRepresentation.setLoginTheme("ormt");
        masterRealmRepresentation.setEmailTheme("ormt");
        masterRealmResource.update(masterRealmRepresentation);
        log.info("### KEYCLOAK: Setting master realm Done  ... ###");

    }

    private RealmResource setupRealm(Keycloak keycloak, String realmName) {
        log.info("### KEYCLOAK: Setting realm   ... ###");
        keycloakRealmService.deleteRealm(keycloak, realmName);
        RealmResource realmResource = keycloakRealmService.createRealm(keycloak, realmName);
        log.info("### KEYCLOAK: Setting realm Done  ... ###");

        return realmResource;

    }

    private void setupBackendClient(RealmResource realmResource, String clientId, String clientName, String rootUrl,
            String secret) {
        log.info("### KEYCLOAK: Create backend Client   ... ###");
        ClientRepresentation clientRepresentation = new ClientRepresentation();
        clientRepresentation.setClientId(clientId);
        clientRepresentation.setName(clientName);
        clientRepresentation.setRootUrl(rootUrl);
        clientRepresentation.setRedirectUris(Arrays.asList(rootUrl + "/*"));
        clientRepresentation.setAdminUrl(rootUrl);
        clientRepresentation.setWebOrigins(Arrays.asList(rootUrl));
        clientRepresentation.setPublicClient(false);
        clientRepresentation.setDirectAccessGrantsEnabled(true);
        clientRepresentation.setAlwaysDisplayInConsole(false);
        clientRepresentation.setSecret(secret);
        createClient(realmResource, clientRepresentation);

        log.info("### KEYCLOAK: Create backend Client Done  ... ###");
    }

    private void setupFrontendClient(RealmResource realmResource, String frontendClientId, String clientName,
            String frontendRootUrl,
            String secret) {
        log.info("### KEYCLOAK: Create frontend Client   ... ###");

        ClientRepresentation clientRepresentation = new ClientRepresentation();
        clientRepresentation.setClientId(frontendClientId);
        clientRepresentation.setName(clientName);
        clientRepresentation.setRootUrl(frontendRootUrl);
        clientRepresentation.setRedirectUris(Arrays.asList(frontendRootUrl + "/*"));
        clientRepresentation.setAdminUrl(frontendRootUrl);
        clientRepresentation.setBaseUrl(frontendRootUrl);
        clientRepresentation.setWebOrigins(Arrays.asList(frontendRootUrl));
        clientRepresentation.setPublicClient(true);
        clientRepresentation.setDirectAccessGrantsEnabled(true);
        clientRepresentation.setSecret(secret);
        clientRepresentation.setAlwaysDisplayInConsole(true);
        clientRepresentation.setFrontchannelLogout(true);

        createClient(realmResource, clientRepresentation);
        log.info("### KEYCLOAK: Create frontend Client Done  ... ###");
    }

    private ClientResource createClient(RealmResource realmResource, ClientRepresentation clientRepresentation) {
        if (keycloakClientService.clientExists(realmResource, clientRepresentation.getClientId())) {
            keycloakClientService.deleteClient(realmResource, clientRepresentation.getClientId());
        }
        return keycloakClientService.createClient(realmResource, clientRepresentation);
    }

}
