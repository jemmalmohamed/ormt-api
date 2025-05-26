package ma.org.ormt.modules.users.auth.impl;

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
import ma.org.ormt.modules.users.auth.AuthService;
import ma.org.ormt.security.dtos.AuthorisationDto;
import ma.org.ormt.security.dtos.CreateRoleRequestDto;
import ma.org.ormt.security.dtos.PermissionDto;
import ma.org.ormt.security.dtos.ResourceDto;
import ma.org.ormt.security.dtos.RoleDto;
import ma.org.ormt.security.dtos.RoleWithPermissionsDto;
import ma.org.ormt.security.keycloak.config.KeycloakConnectService;
import ma.org.ormt.security.keycloak.services.realm.KeycloakRealmService;
import ma.org.ormt.security.keycloak.services.roles.client.KeycloakClientRoleService;

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
    public List<ResourceDto> getResourcesWithPermissions() {
        List<ResourceDto> resources = new ArrayList<>();
        Keycloak keycloak = keycloakService.getKeyCloakAdminCli();
        keycloakRealmService.getClientResource(keycloak, "ormt", "ormt-api")
                .ifPresent(clientResource -> {
                    clientResource.authorization().resources().resources().forEach(resource -> {
                        ResourceDto dto = new ResourceDto();
                        dto.setName(resource.getName());
                        dto.setDisplayName(resource.getDisplayName());
                        dto.setScopes(
                                resource.getScopes() != null
                                        ? resource.getScopes().stream().map(scope -> scope.getName())
                                                .collect(Collectors.toList())
                                        : new ArrayList<>());
                        resources.add(dto);
                    });
                });
        keycloak.tokenManager().logout();
        keycloak.close();
        return resources;
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
        List<Map<String, Object>> policies = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        Keycloak keycloak = keycloakService.getKeyCloakAdminCli();
        keycloakRealmService.getClientResource(keycloak, realmName, backendClientName)
                .ifPresent(clientResource -> {
                    List<?> allPolicies = clientResource.authorization().policies().policies();
                    for (Object policy : allPolicies) {
                        if (policy instanceof org.keycloak.representations.idm.authorization.PolicyRepresentation pol) {
                            if ("role".equals(pol.getType())) {
                                Map<String, Object> map = objectMapper.convertValue(pol,
                                        new TypeReference<Map<String, Object>>() {
                                        });
                                // Parse config.roles JSON if present
                                Object configObj = map.get("config");
                                if (configObj instanceof Map) {
                                    Map<String, Object> config = (Map<String, Object>) configObj;
                                    Object rolesObj = config.get("roles");
                                    if (rolesObj instanceof String) {
                                        String rolesJson = (String) rolesObj;
                                        try {
                                            List<Map<String, Object>> rolesList = objectMapper.readValue(rolesJson,
                                                    new TypeReference<List<Map<String, Object>>>() {
                                                    });
                                            map.put("roleReferences", rolesList);
                                        } catch (Exception e) {
                                            map.put("roleReferences", new ArrayList<>());
                                        }
                                    }
                                }
                                policies.add(map);
                            }
                        }
                    }
                });
        keycloak.tokenManager().logout();
        keycloak.close();
        return policies;
    }

    /**
     * Update a policy's config directly (e.g., for advanced or bulk updates).
     * 
     * @param policyId  the ID of the policy
     * @param newConfig the new config map to set on the policy
     * @return true if successful, false otherwise
     */
    public boolean assignRoleToPolicyConfig(String policyId, Map<String, String> newConfig) {
        Keycloak keycloak = keycloakService.getKeyCloakAdminCli();
        boolean result = false;
        try {
            result = keycloakRealmService.getClientResource(keycloak, realmName, backendClientName)
                    .map(clientResource -> {
                        try {
                            var policy = clientResource.authorization().policies().policy(policyId).toRepresentation();
                            if (policy == null || !"role".equals(policy.getType())) {
                                log.error("Policy '{}' is null or not of type 'role'", policyId);
                                return false;
                            }
                            policy.setConfig(newConfig);
                            clientResource.authorization().policies().policy(policyId).update(policy);
                            log.info("Successfully updated policy '{}' config", policyId);
                            return true;
                        } catch (Exception e) {
                            log.error("Failed to update policy '{}' config: {}", policyId, e.getMessage(), e);
                            return false;
                        }
                    }).orElse(false);
        } finally {
            keycloak.tokenManager().logout();
            keycloak.close();
        }
        return result;
    }

    /**
     * Assign a role to multiple policies (role policy type) in Keycloak.
     * 
     * @param policyIds the list of policy IDs
     * @param roleName  the name of the role to assign
     * @return true if all assignments succeed, false otherwise
     */
    public boolean assignRoleToPolicies(List<String> policyIds, String roleName) {
        boolean allSuccess = true;
        for (String policyId : policyIds) {
            boolean result = assignRoleToPolicy(policyId, roleName);
            if (!result) {
                allSuccess = false;
            }
        }
        return allSuccess;
    }

    @Override
    public boolean assignRoleToPolicy(String policyId, String roleName) {
        Keycloak keycloak = keycloakService.getKeyCloakAdminCli();
        boolean result = false;
        try {
            result = keycloakRealmService.getClientResource(keycloak, realmName, backendClientName)
                    .map(clientResource -> {
                        try {
                            var policy = clientResource.authorization().policies().policy(policyId).toRepresentation();
                            if (policy == null || !"role".equals(policy.getType())) {
                                log.error("Policy '{}' is null or not of type 'role'", policyId);
                                return false;
                            }

                            Map<String, String> config = policy.getConfig();
                            String rolesJson = config.getOrDefault("roles", "[]");
                            ObjectMapper objectMapper = new ObjectMapper();
                            List<Map<String, Object>> rolesList = objectMapper.readValue(rolesJson,
                                    new TypeReference<List<Map<String, Object>>>() {
                                    });
                            // Fetch the role
                            RoleRepresentation roleRep = clientResource.roles().get(roleName).toRepresentation();
                            if (roleRep == null) {
                                log.error("Role '{}' not found", roleName);
                                return false;
                            }

                            // Check if already present by ID
                            boolean alreadyPresent = rolesList.stream()
                                    .anyMatch(r -> roleRep.getId().equals(r.get("id")));
                            if (alreadyPresent) {
                                log.info("Role '{}' already present in policy '{}', skipping update.", roleName,
                                        policyId);
                                return true;
                            }

                            // Add new role with same structure as creation method
                            Map<String, Object> newRole = new java.util.HashMap<>();
                            newRole.put("id", roleRep.getId());
                            newRole.put("required", Boolean.FALSE);
                            rolesList.add(newRole);

                            String updatedRolesJson = objectMapper.writeValueAsString(rolesList);
                            config.put("roles", updatedRolesJson);

                            return assignRoleToPolicyConfig(policyId, config);
                        } catch (Exception e) {
                            log.error("Error updating policy '{}': {}", policyId, e.getMessage(), e);
                            return false;
                        }
                    }).orElse(false);
        } finally {
            keycloak.tokenManager().logout();
            keycloak.close();
        }
        return result;
    }
}