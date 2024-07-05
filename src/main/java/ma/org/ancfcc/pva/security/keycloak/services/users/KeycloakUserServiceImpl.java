package ma.org.ancfcc.pva.security.keycloak.services.users;

import java.io.InputStream;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import ma.org.ancfcc.pva.core.exceptions.handlers.KeycloakException;
import ma.org.ancfcc.pva.security.keycloak.representation.UserJsonRepresentation;

@Service
@RequiredArgsConstructor
public class KeycloakUserServiceImpl implements KeycloakUserService {

    @Override
    public Optional<UserRepresentation> findUserByUserName(RealmResource realmResource, String userName) {
        return realmResource.users().searchByUsername(userName, true).stream().findFirst();
    }

    @Override
    public UserRepresentation createKeycloakUser(RealmResource realmResource, UserRepresentation userRepresentation) {

        try (Response response = realmResource.users().create(userRepresentation)) {
            if (response.getStatus() != 201) {
                throw new KeycloakException("Failed to create user");
            } else {
                Optional<UserRepresentation> optionalUser = this.findUserByUserName(realmResource,
                        userRepresentation.getUsername());
                if (optionalUser.isPresent()) {
                    return optionalUser.get();
                } else {
                    throw new KeycloakException("Failed to find created user");
                }
            }
        }
    }

    @Override
    public void enableUser(RealmResource realmResource, String userName) {
        Optional<UserRepresentation> userRepresentation = findUserByUserName(realmResource, userName);
        if (userRepresentation.isPresent()) {
            UserRepresentation user = userRepresentation.get();
            user.setEnabled(true);
            realmResource.users().get(user.getId()).update(user);
        }
    }

    @Override
    public void addClientRoleToUser(UserResource userResource, String clientUUID,
            RoleRepresentation roleRepresentation) {

        userResource.roles().clientLevel(clientUUID).add(Arrays.asList(roleRepresentation));

    }

    @Override
    public CredentialRepresentation createPasswordCredentials(String password) {
        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue(password);
        passwordCredentials.setTemporary(false);
        return passwordCredentials;
    }

    @Override
    public boolean userExists(RealmResource realmResource, String userName) {
        return findUserByUserName(realmResource, userName).isPresent();
    }

    @Override
    public void deleteUser(RealmResource realmResource, String userName) {
        Optional<UserRepresentation> userRepresentation = findUserByUserName(realmResource, userName);
        if (userRepresentation.isPresent()) {
            realmResource.users().delete(userRepresentation.get().getId());
        }
    }

    @Override
    public List<UserJsonRepresentation> getUsersRepresentation(String usersJsonResources) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            InputStream inputStream = getClass().getClassLoader()
                    .getResourceAsStream(usersJsonResources);

            return mapper.readValue(inputStream, new TypeReference<List<UserJsonRepresentation>>() {
            });

        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    public List<SimpleEntry<UserRepresentation, String>> getUsersWithRoles(String usersJsonResources) {
        try {
            List<UserJsonRepresentation> users = getUsersRepresentation(usersJsonResources);

            List<SimpleEntry<UserRepresentation, String>> userEntries = new ArrayList<>();
            for (UserJsonRepresentation userJson : users) {
                UserRepresentation userRepresentation = mapToUserRepresentation(userJson);
                SimpleEntry<UserRepresentation, String> userEntry = new SimpleEntry<>(
                        userRepresentation, userJson.getClientRoles().get(0));
                userEntries.add(userEntry);
            }

            return userEntries;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private UserRepresentation mapToUserRepresentation(UserJsonRepresentation userJson) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(userJson.getUsername());
        user.setFirstName(userJson.getFirstName());
        user.setLastName(userJson.getLastName());
        user.setEmail(userJson.getEmail());
        user.setEnabled(userJson.getEnabled());
        user.setEmailVerified(userJson.getEmailVerified());
        user.setCredentials(List.of(createPasswordCredentials(userJson.getPassword())));

        return user;
    }

}
