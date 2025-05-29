package ma.org.ormt.security.keycloak.services.roles.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ma.org.ormt.core.validators.ObjectsValidator;
import ma.org.ormt.modules.espaces.dtos.request.EspaceRequestDto;
import ma.org.ormt.security.keycloak.services.KeycloakConnectService;
import ma.org.ormt.security.keycloak.services.realm.KeycloakRealmService;
import ma.org.ormt.security.roles.dto.RoleDto;
import ma.org.ormt.security.roles.dto.request.RoleRequestDto;

@Service
public class KeycloakClientRoleServiceImpl implements KeycloakClientRoleService {

    @Value("${keycloak.realm}")
    private String realmName;

    @Value("${keycloak.clients.backend.id}")
    private String backendClientName;

    @Autowired
    private KeycloakRealmService keycloakRealmService;

    @Autowired
    private KeycloakConnectService keycloakService;

    @Autowired
    private ObjectsValidator<RoleRequestDto> validator;

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
    public RoleRepresentation createClientRole(RoleRequestDto createRoleRequestDto) {
        validator.validate(createRoleRequestDto);
        Keycloak keycloak = keycloakService.getKeyCloakAdminCli();
        ClientResource clientResource = keycloakRealmService.getClientResource(keycloak, realmName, backendClientName)
                .orElseThrow(() -> new IllegalStateException("Client resource not found"));
        if (roleClientExists(clientResource, createRoleRequestDto.getName())) {
            return null;
        }
        RoleRepresentation roleRepresentation = new RoleRepresentation();
        roleRepresentation.setName(createRoleRequestDto.getName());
        roleRepresentation.setDescription(createRoleRequestDto.getDescription());
        roleRepresentation.setClientRole(true);
        clientResource.roles().create(roleRepresentation);

        return clientResource.roles().get(roleRepresentation.getName()).toRepresentation();
    }

    @Override
    public RoleRepresentation updateClientRole(RoleRequestDto createRoleRequestDto) {
        validator.validate(createRoleRequestDto);

        Keycloak keycloak = keycloakService.getKeyCloakAdminCli();

        ClientResource clientResource = keycloakRealmService.getClientResource(keycloak, realmName, backendClientName)
                .orElseThrow(() -> new IllegalStateException("Client resource not found"));

        // Check if the role exists using id - for update, it should exist
        RoleRepresentation roleRepresentation = findRoleClientById(createRoleRequestDto.getId())
                .orElseThrow(() -> new IllegalStateException("Role not found: " + createRoleRequestDto.getId()));

        // Get the role resource by ID
        RoleResource roleResource = clientResource.roles().get(roleRepresentation.getName());

        // Update the role properties
        roleRepresentation.setName(createRoleRequestDto.getName());
        roleRepresentation.setDescription(createRoleRequestDto.getDescription());

        // Update the role
        roleResource.update(roleRepresentation);

        return roleResource.toRepresentation();
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
    public Optional<RoleRepresentation> findRoleClientByName(String roleName) {
        Keycloak keycloak = keycloakService.getKeyCloakAdminCli();
        ClientResource clientResource = keycloakRealmService.getClientResource(keycloak, realmName, backendClientName)
                .orElseThrow(() -> new IllegalStateException("Client resource not found"));
        return clientResource.roles().list().stream().filter(role -> role.getName().equals(roleName)).findFirst();
    }

    @Override
    public Optional<RoleRepresentation> findRoleClientById(String id) {
        Keycloak keycloak = keycloakService.getKeyCloakAdminCli();
        ClientResource clientResource = keycloakRealmService.getClientResource(keycloak, realmName, backendClientName)
                .orElseThrow(() -> new IllegalStateException("Client resource not found"));
        return clientResource.roles().list().stream().filter(role -> role.getId().equals(id)).findFirst();
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

    @Override
    public void deleteClientRole(String id) {
        RoleRepresentation roleRepresentation = findRoleClientById(id)
                .orElseThrow(() -> new IllegalStateException("Role not found with id: " + id));
        Keycloak keycloak = keycloakService.getKeyCloakAdminCli();
        ClientResource clientResource = keycloakRealmService.getClientResource(keycloak, realmName, backendClientName)
                .orElseThrow(() -> new IllegalStateException("Client resource not found"));
        clientResource.roles().deleteRole(roleRepresentation.getName());
        keycloak.tokenManager().logout();
        keycloak.close();
    }

    @Override
    public List<RoleDto> getClientRoles() {
        List<RoleDto> roles = new ArrayList<>();
        Keycloak keycloak = keycloakService.getKeyCloakAdminCli();

        keycloakRealmService.getClientResource(keycloak, realmName, backendClientName)
                .ifPresent(clientResource -> {
                    clientResource.roles().list().forEach(roleRepresentation -> {
                        if (roleRepresentation.getName() != null) {
                            RoleDto roleDto = new RoleDto();
                            roleDto.setId(roleRepresentation.getId());
                            roleDto.setName(roleRepresentation.getName());
                            roleDto.setDescription(roleRepresentation.getDescription());
                            roles.add(roleDto);
                        }
                    });
                });
        keycloak.tokenManager().logout();
        keycloak.close();

        return roles;
    }

}
