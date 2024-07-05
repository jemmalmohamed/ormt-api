package ma.org.ormt.security.keycloak.services.roles.client;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.stereotype.Service;

@Service
public class KeycloakClientRoleServiceImpl implements KeycloakClientRoleService {

    @Override
    public RoleResource createClientRole(ClientResource clientResource, String roleName) {
        return createClientRole(clientResource, roleName, roleName);
    }

    @Override
    public RoleResource createClientRole(ClientResource clientResource, String roleName, String description) {
        if (roleClientExists(clientResource, roleName)) {
            return null;
        }
        RoleRepresentation roleRepresentation = new RoleRepresentation();
        roleRepresentation.setName(roleName);
        roleRepresentation.setDescription(description);
        roleRepresentation.setClientRole(true);
        clientResource.roles().create(roleRepresentation);

        return clientResource.roles().get(roleRepresentation.getName());
    }

    @Override
    public boolean roleClientExists(ClientResource clientResource, String roleName) {
        return clientResource.roles().list().stream().anyMatch(role -> role.getName().equals(roleName));
    }

    @Override
    public Optional<RoleRepresentation> findRoleClientByName(ClientResource clientResource, String roleName) {
        return clientResource.roles().list().stream().filter(role -> role.getName().equals(roleName)).findFirst();
    }

    @Override
    public List<RoleRepresentation> getClientRoles(ClientResource clientResource, List<String> rolesNames) {
        return clientResource.roles().list().stream().filter(role -> rolesNames.contains(role.getName()))
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public List<String> getClientRolesIds(ClientResource clientResource, List<String> rolesNames) {
        return clientResource.roles().list().stream().filter(role -> rolesNames.contains(role.getName()))
                .map(RoleRepresentation::getId).collect(Collectors.toList());
    }

}
