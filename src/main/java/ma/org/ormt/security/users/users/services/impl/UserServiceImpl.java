package ma.org.ormt.security.users.users.services.impl;

import java.util.List;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.log4j.Log4j2;
import ma.org.ormt.core.exceptions.handlers.KeycloakException;
import ma.org.ormt.core.validators.ObjectsValidator;
import ma.org.ormt.security.keycloak.services.KeycloakConnectService;
import ma.org.ormt.security.keycloak.services.realm.KeycloakRealmService;
import ma.org.ormt.security.keycloak.services.roles.client.KeycloakClientRoleService;
import ma.org.ormt.security.keycloak.services.users.KeycloakUserService;
import ma.org.ormt.security.users.users.dtos.request.UserRequestDto;
import ma.org.ormt.security.users.users.services.UserService;

@Service
@Transactional
@Log4j2
public class UserServiceImpl implements UserService {

    @Value("${keycloak.realm}")
    private String realmName;

    @Value("${keycloak.clients.backend.id}")
    private String backendClientName;

    @Autowired
    private KeycloakUserService keycloakUserService;

    @Autowired
    private KeycloakConnectService keycloakConnectService;

    @Autowired
    private KeycloakRealmService keycloakRealmService;
    @Autowired
    private KeycloakClientRoleService keycloakClientRoleService;

    @Autowired
    private ObjectsValidator<UserRequestDto> validator;

    @Override
    public UserRepresentation create(UserRequestDto requestDto) throws Exception {
        // Validate the request DTO
        validator.validate(requestDto);

        // Get realm resource
        RealmResource realmResource = getRealmResource();

        ClientResource backendClientResource = realmResource.clients()
                .get(backendClientName);

        // Check if user exists and delete it (similar to seeder approach)
        if (keycloakUserService.userExists(realmResource, requestDto.getUsername())) {

        }

        // Create user representation
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(requestDto.getUsername());
        userRepresentation.setFirstName(requestDto.getFirstName());
        userRepresentation.setLastName(requestDto.getLastName());
        userRepresentation.setEmail(requestDto.getEmail());
        userRepresentation.setEnabled(requestDto.getEnabled());
        userRepresentation.setEmailVerified(true);

        // Create password credential
        CredentialRepresentation credential = keycloakUserService.createPasswordCredentials(
                requestDto.getPassword());

        userRepresentation.setCredentials(List.of(credential));

        // Set client roles if provided
        if (requestDto.getClientRoles() != null) {
            for (String clientId : requestDto.getClientRoles().keySet()) {
                List<String> roles = requestDto.getClientRoles().get(clientId);
                for (String roleName : roles) {

                    RoleRepresentation roleRepresentation = keycloakClientRoleService
                            .findRoleClientByName(backendClientResource, roleName).get();

                    if (roleRepresentation != null) {
                        keycloakUserService.addClientRoleToUser(
                                realmResource.users().get(userRepresentation.getId()),
                                clientId, roleRepresentation);
                    }
                }
            }
        }
        // Create the user using the same method as in the seeder
        UserRepresentation createdUser = keycloakUserService.createKeycloakUser(realmResource, userRepresentation);

        return createdUser;
    }

    @Override
    public void assignRoleToUser(RealmResource realmResource,
            ClientResource clientResource, RoleRepresentation roleRepresentation,
            UserRepresentation userRepresentation) {
        keycloakUserService.addClientRoleToUser(
                realmResource.users().get(userRepresentation.getId()),
                clientResource.toRepresentation().getId(),
                roleRepresentation);
    }

    private RealmResource getRealmResource() {
        Keycloak keycloak = keycloakConnectService.getKeyCloakAdminCli();
        if (keycloak == null) {
            throw new KeycloakException("Keycloak connection is null");
        }
        RealmResource realmResource = keycloakRealmService.getRealmResource(keycloak,
                realmName);
        return realmResource;
    }

}