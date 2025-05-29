package ma.org.ormt.security.users.users.services;

import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import ma.org.ormt.security.users.users.dtos.request.UserRequestDto;

public interface UserService {

    UserRepresentation create(UserRequestDto requestDto) throws Exception;

    void assignRoleToUser(RealmResource realmResource,
            ClientResource clientResource, RoleRepresentation roleRepresentation,
            UserRepresentation userRepresentation);

}