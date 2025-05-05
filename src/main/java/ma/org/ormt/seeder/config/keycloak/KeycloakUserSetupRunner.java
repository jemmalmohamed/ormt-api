package ma.org.ormt.seeder.config.keycloak;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ma.org.ormt.security.keycloak.config.KeycloakConnectService;
import ma.org.ormt.security.keycloak.services.realm.KeycloakRealmService;
import ma.org.ormt.security.keycloak.services.roles.client.KeycloakClientRoleService;
import ma.org.ormt.security.keycloak.services.roles.enums.KeycloakRole;
import ma.org.ormt.security.keycloak.services.users.KeycloakUserService;

@Log4j2
@Configuration
@RequiredArgsConstructor
@Order(2)
public class KeycloakUserSetupRunner implements CommandLineRunner {

    private final KeycloakRealmService keycloakRealmService;

    private final KeycloakUserService keycloakUserService;
    private final KeycloakConnectService keycloakService;

    private final KeycloakClientRoleService keycloakClientRoleService;
    @Value("${keycloak.realm}")
    private String realmName;

    @Value("${keycloak.clients.backend.id}")
    private String backendClientName;

    @Value("${starter.keycloak.seed}")
    private String seedKeycloak;

    @Override
    public void run(String... args) throws Exception {

        if (!seedKeycloak.equals("true")) {
            log.info("### KEYCLOAK: Skipping keycloak users seeding");
            return;
        }

        Keycloak keycloak = keycloakService.getKeyCloakAdminCli();
        log.info("### KEYCLOAK: Setting up keycloak Users ... ###");
        RealmResource realmResource = keycloakRealmService.getRealmResource(keycloak,
                realmName);

        ClientResource backendClientResource = keycloakRealmService.getClientResource(keycloak, realmName,
                backendClientName).orElse(null);

        List<SimpleEntry<UserRepresentation, String>> usersWithRoles = keycloakUserService.getUsersWithRolesFromJson(
                "init-data/authentication/users.json");

        Map<String, String> rolesMap = getRolesMap();
        setupClientRoles(backendClientResource, rolesMap);

        setupDefaultUsers(realmResource, backendClientResource, usersWithRoles);
        keycloak.tokenManager().logout();
        keycloak.close();
        log.info("### KEYCLOAK: setup Users completed. ###");

    }

    private Map<String, String> getRolesMap() {
        return Map.of(
                KeycloakRole.role_admin.getRoleName(), "administrateur ormt",
                KeycloakRole.role_public.getRoleName(), "public ormt",
                KeycloakRole.role_decideur.getRoleName(), "décideur ormt");
    }

    private void setupClientRoles(ClientResource clientResource, Map<String, String> rolesMap) {
        Set<String> existingRoles = rolesMap.keySet();
        for (String role : existingRoles) {
            keycloakClientRoleService.createClientRole(clientResource, role, rolesMap.get(role));
        }
    }

    private void setupDefaultUsers(RealmResource realmResource, ClientResource clientResource,
            List<SimpleEntry<UserRepresentation, String>> usersWithRoles) {

        for (SimpleEntry<UserRepresentation, String> userWithRole : usersWithRoles) {

            if (keycloakUserService.userExists(realmResource, userWithRole.getKey().getUsername())) {
                log.info("### KEYCLOAK: User {} already exists", userWithRole.getKey().getUsername());
                keycloakUserService.deleteUser(realmResource, userWithRole.getKey().getUsername());
            }

            UserRepresentation userRepresentation = keycloakUserService.createKeycloakUser(realmResource,
                    userWithRole.getKey());

            RoleRepresentation roleRepresentation = keycloakClientRoleService
                    .findRoleClientByName(clientResource, userWithRole.getValue()).get();

            assignRoleToUser(realmResource, clientResource, roleRepresentation, userRepresentation);
        }
    }

    void assignRoleToUser(RealmResource realmResource,
            ClientResource clientResource, RoleRepresentation roleRepresentation,
            UserRepresentation userRepresentation) {
        keycloakUserService.addClientRoleToUser(
                realmResource.users().get(userRepresentation.getId()),
                clientResource.toRepresentation().getId(),
                roleRepresentation);
    }

    CredentialRepresentation createPasswordCredentials(String password) {
        return keycloakUserService.createPasswordCredentials(password);
    }

}
