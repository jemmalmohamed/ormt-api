package ma.org.ormt.security.keycloak.services.roles.client;

import java.util.List;
import java.util.Optional;

import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.representations.idm.RoleRepresentation;

public interface KeycloakClientRoleService {

    boolean roleClientExists(ClientResource clientResource, String roleName);

    Optional<RoleRepresentation> findRoleClientByName(ClientResource clientResource, String roleName);

    RoleResource createClientRole(ClientResource clientResource, String roleName, String description);

    RoleResource createClientRole(ClientResource clientResource, String roleName);

    List<RoleRepresentation> getClientRoles(ClientResource clientResource, List<String> rolesNames);

    List<String> getClientRolesIds(ClientResource clientResource, List<String> rolesNames);
}
