package ma.org.ancfcc.pva.security.keycloak.services.authorization.permission;

import org.keycloak.representations.idm.authorization.ScopePermissionRepresentation;

public interface KeycloakPermissionService {

    void createPermission(ScopePermissionRepresentation permission, String realmName, String client);
}
