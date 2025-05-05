package ma.org.ormt.security.keycloak.services.authorization.permission;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.representations.idm.authorization.ScopePermissionRepresentation;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ma.org.ormt.security.keycloak.config.KeycloakConnectService;
import ma.org.ormt.security.keycloak.services.realm.KeycloakRealmService;

@Service
@RequiredArgsConstructor
public class KeycloakPermissionServiceImpl implements KeycloakPermissionService {

    private final KeycloakConnectService keycloakService;
    private final KeycloakRealmService keycloakRealmService;

    @Override
    public void createPermission(ScopePermissionRepresentation permission, String realmName, String client) {
        Keycloak keycloak = keycloakService.getKeyCloakAdminCli();
        ClientResource clientResource = keycloakRealmService
                .getClientResource(keycloak, realmName, client)
                .orElse(null);
        if (clientResource != null) {
            clientResource.authorization().permissions().scope().create(permission);
        }
        keycloak.tokenManager().logout();
        keycloak.close();
    }

}
