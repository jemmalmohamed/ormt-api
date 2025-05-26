
package ma.org.ormt.security.keycloak.services.users;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Optional;

import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import ma.org.ormt.security.keycloak.dto.UserJsonRepresentation;

@Service
public interface KeycloakUserService {

    UserRepresentation createKeycloakUser(RealmResource realmResource, UserRepresentation userRepresentation);

    Optional<UserRepresentation> findUserByUserName(RealmResource realmResource, String userName);

    public Optional<UserRepresentation> findUserById(RealmResource realmResource, String userId);

    void enableUser(RealmResource realmResource, String userName);

    void addClientRoleToUser(UserResource userResource, String clientId, RoleRepresentation roleRepresentation);

    CredentialRepresentation createPasswordCredentials(String password);

    boolean userExists(RealmResource realmResource, String userName);

    void deleteUser(RealmResource realmResource, String userName);

    List<SimpleEntry<UserRepresentation, String>> getUsersWithRolesFromJson(String usersJsonResources);

    List<UserJsonRepresentation> getUsersRepresentationFromJson(String usersJsonResources);

}