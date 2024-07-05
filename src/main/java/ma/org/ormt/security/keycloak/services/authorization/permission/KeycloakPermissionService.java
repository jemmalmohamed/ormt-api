package ma.org.ormt.security.keycloak.services.authorization.permission;

import org.keycloak.representations.idm.authorization.ScopePermissionRepresentation;

public interface KeycloakPermissionService {

    void createPermission(ScopePermissionRepresentation permission, String realmName, String client);
}
