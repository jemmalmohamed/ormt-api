package ma.org.ormt.security.keycloak.services.roles.client;

import java.util.List;
import java.util.Optional;

import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.representations.idm.RoleRepresentation;

import ma.org.ormt.security.roles.dto.RoleDto;
import ma.org.ormt.security.roles.dto.request.RoleRequestDto;

public interface KeycloakClientRoleService {

    boolean roleClientExists(ClientResource clientResource, String roleName);

    Optional<RoleRepresentation> findRoleClientByName(ClientResource clientResource, String roleName);

    Optional<RoleRepresentation> findRoleClientByName(String roleName);

    public Optional<RoleRepresentation> findRoleClientById(String id);

    RoleResource createClientRole(ClientResource clientResource, String roleName, String description);

    RoleResource createClientRole(ClientResource clientResource, String roleName);

    RoleRepresentation createClientRole(RoleRequestDto roleRequestDto);

    RoleRepresentation updateClientRole(RoleRequestDto roleRequestDto);

    List<RoleRepresentation> getClientRoles(ClientResource clientResource, List<String> rolesNames);

    List<String> getClientRolesIds(ClientResource clientResource, List<String> rolesNames);

    public List<RoleDto> getClientRoles();

    public void deleteClientRole(String id);

}
