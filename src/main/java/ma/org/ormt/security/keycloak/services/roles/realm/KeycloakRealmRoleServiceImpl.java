package ma.org.ormt.security.keycloak.services.roles.realm;

import java.util.Optional;

import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.stereotype.Service;

@Service
public class KeycloakRealmRoleServiceImpl implements KeycloakRealmRoleService {

    @Override
    public Optional<RoleRepresentation> findRoleRealmByName(RealmResource realmResource, String roleName) {
        return realmResource.roles().list().stream().filter(role -> role.getName().equals(roleName)).findFirst();
    }

    @Override
    public RoleResource createRealmRole(RealmResource realmResource, RoleRepresentation roleRepresentation) {
        if (roleRealmExists(realmResource, roleRepresentation.getName())) {
            return null;
        }
        realmResource.roles().create(roleRepresentation);

        return realmResource.roles().get(roleRepresentation.getName());
    }

    @Override
    public boolean roleRealmExists(RealmResource realmResource, String roleName) {
        return realmResource.roles().list().stream().anyMatch(role -> role.getName().equals(roleName));
    }

}
