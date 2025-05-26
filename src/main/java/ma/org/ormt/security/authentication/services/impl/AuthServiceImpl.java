package ma.org.ormt.security.authentication.services.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ma.org.ormt.security.authentication.services.AuthService;
import ma.org.ormt.security.authorization.dto.AuthorisationDto;
import ma.org.ormt.security.authorization.dto.PermissionDto;
import ma.org.ormt.security.authorization.dto.ResourceDto;
import ma.org.ormt.security.authorization.dto.RoleWithPermissionsDto;
import ma.org.ormt.security.keycloak.services.KeycloakConnectService;
import ma.org.ormt.security.keycloak.services.realm.KeycloakRealmService;
import ma.org.ormt.security.keycloak.services.roles.client.KeycloakClientRoleService;
import ma.org.ormt.security.keycloak.services.authorization.policy.KeycloakPolicyService;
import ma.org.ormt.security.keycloak.services.authorization.resource.KeycloakResourceService;
import ma.org.ormt.security.roles.dto.CreateRoleRequestDto;
import ma.org.ormt.security.roles.dto.RoleDto;

@Service
@RequiredArgsConstructor
@Log4j2
public class AuthServiceImpl implements AuthService {

    @Value("${keycloak.realm}")
    private String realmName;

    @Value("${keycloak.clients.backend.id}")
    private String backendClientName;

    private final KeycloakRealmService keycloakRealmService;
    private final KeycloakConnectService keycloakService;
    public final KeycloakClientRoleService keycloakClientRoleService;
    private final KeycloakPolicyService keycloakPolicyService;
    private final KeycloakResourceService keycloakResourceService;

    private static final String ROLE_PREFIX = "ROLE_";
    private static final String SCOPE_PREFIX = "SCOPE_";

    public AuthorisationDto getCurrentUserAuth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        List<RoleDto> roles = new ArrayList<>();
        List<PermissionDto> permissions = new ArrayList<>();
        Collection<? extends GrantedAuthority> Authorities = authentication.getAuthorities();
        Authorities.forEach(authority -> {
            String auth = authority.getAuthority();
            if (auth.startsWith(ROLE_PREFIX)) {
                roles.add(createRoleDto(auth));
            } else if (!auth.startsWith(SCOPE_PREFIX)) {
                permissions.add(createResourcePermission(auth));
            }
        });

        return new AuthorisationDto(roles, permissions);
    }

    private RoleDto createRoleDto(String authority) {
        String roleName = authority.substring(ROLE_PREFIX.length());
        RoleDto roleDto = new RoleDto();
        roleDto.setRole(roleName);
        // id is not available from authority string, so leave as null
        return roleDto;
    }

    private PermissionDto createResourcePermission(String authority) {
        String[] parts = authority.split(":");
        return new PermissionDto(
                parts[0],
                parts[1]);
    }

    public AuthorisationDto getAppRoles() {
        List<RoleDto> roles = new ArrayList<>();
        Keycloak keycloak = keycloakService.getKeyCloakAdminCli();
        keycloakRealmService.getClientResource(keycloak, realmName, backendClientName)
                .ifPresent(clientResource -> {
                    clientResource.roles().list().forEach(roleRepresentation -> {
                        if (roleRepresentation.getName() != null) {
                            RoleDto roleDto = new RoleDto();
                            roleDto.setId(roleRepresentation.getId());
                            roleDto.setRole(roleRepresentation.getName());
                            roles.add(roleDto);
                        }
                    });
                });
        keycloak.tokenManager().logout();
        keycloak.close();

        return new AuthorisationDto(roles, null);
    }

    @Override
    public List<RoleWithPermissionsDto> getRolesWithPermissions() {
        List<RoleWithPermissionsDto> roles = new ArrayList<>();
        Keycloak keycloak = keycloakService.getKeyCloakAdminCli();
        keycloakRealmService.getClientResource(keycloak, "ormt", "ormt-api")
                .ifPresent(clientResource -> {
                    clientResource.roles().list().forEach(roleRepresentation -> {
                        if (roleRepresentation.getName().startsWith("role_")) {
                            RoleWithPermissionsDto dto = new RoleWithPermissionsDto();
                            dto.setRoleName(roleRepresentation.getName());
                            dto.setDescription(roleRepresentation.getDescription());
                            // Permissions: get composite roles (client roles assigned to this role)
                            List<String> permissions = clientResource.roles().get(roleRepresentation.getName())
                                    .getRoleComposites().stream().map(r -> r.getName()).collect(Collectors.toList());
                            dto.setPermissions(permissions);
                            roles.add(dto);
                        }
                    });
                });
        keycloak.tokenManager().logout();
        keycloak.close();
        return roles;
    }

    @Override
    public List<PermissionDto> getAllPermissions() {
        List<PermissionDto> permissions = new ArrayList<>();
        Keycloak keycloak = keycloakService.getKeyCloakAdminCli();
        keycloakRealmService.getClientResource(keycloak,
                realmName, backendClientName)
                .ifPresent(clientResource -> {
                    clientResource.authorization().scopes().scopes().forEach(scope -> {
                        permissions.add(new PermissionDto(scope.getName(), scope.getDisplayName()));
                    });
                });
        keycloak.tokenManager().logout();
        keycloak.close();
        return permissions;
    }

    @Override
    public RoleWithPermissionsDto createRole(CreateRoleRequestDto request) {
        Keycloak keycloak = keycloakService.getKeyCloakAdminCli();

        RoleWithPermissionsDto dto = new RoleWithPermissionsDto();
        keycloakRealmService.getClientResource(keycloak, realmName, backendClientName)
                .ifPresent(clientResource -> {
                    // Create role only if it does not exist
                    if (!keycloakClientRoleService.roleClientExists(clientResource, request.getRoleName())) {
                        var roleRep = new RoleRepresentation();
                        roleRep.setName(request.getRoleName());
                        roleRep.setDescription(request.getDescription());
                        clientResource.roles().create(roleRep);
                    }
                    // Assign permissions (as composites)

                    dto.setRoleName(request.getRoleName());
                    dto.setDescription(request.getDescription());
                });
        keycloak.tokenManager().logout();
        keycloak.close();
        return dto;
    }

    @Override
    public List<Map<String, Object>> getAllPolicies() {
        Keycloak keycloak = keycloakService.getKeyCloakAdminCli();
        try {
            return keycloakRealmService.getClientResource(keycloak, realmName, backendClientName)
                    .map(clientResource -> keycloakPolicyService.getAllPolicies(clientResource))
                    .orElse(new ArrayList<>());
        } finally {
            keycloak.tokenManager().logout();
            keycloak.close();
        }
    }

}