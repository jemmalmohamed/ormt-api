package ma.org.ormt.security.keycloak.services.roles.realm;

import java.util.Optional;

import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.representations.idm.RoleRepresentation;

public interface KeycloakRealmRoleService {

    // ## realm roles
    boolean roleRealmExists(RealmResource realmResource, String roleName);

    Optional<RoleRepresentation> findRoleRealmByName(RealmResource realmResource, String roleName);

    RoleResource createRealmRole(RealmResource realmResource, RoleRepresentation roleRepresentation);

    // ## client roles
    boolean roleClientExists(ClientResource clientResource, String roleName);

    Optional<RoleRepresentation> findRoleClientByName(ClientResource clientResource, String roleName);

    RoleResource createClientRole(ClientResource clientResource, RoleRepresentation roleRepresentation);
}
