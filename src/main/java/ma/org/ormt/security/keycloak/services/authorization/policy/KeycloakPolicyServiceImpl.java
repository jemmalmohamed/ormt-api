package ma.org.ormt.security.keycloak.services.authorization.policy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.authorization.PolicyRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ma.org.ormt.security.keycloak.dto.request.KeycloakResourceRequestDto;
import ma.org.ormt.security.keycloak.dto.request.KeycloakRoleRequestDto;
import ma.org.ormt.security.keycloak.services.KeycloakConnectService;
import ma.org.ormt.security.keycloak.services.realm.KeycloakRealmService;
import ma.org.ormt.security.policy.dto.PolicyDto;
import ma.org.ormt.security.policy.dto.PolicyRoleDto;
import ma.org.ormt.security.policy.dto.RolePoliciesRequestDto;

@Log4j2
@Service
@RequiredArgsConstructor
public class KeycloakPolicyServiceImpl implements KeycloakPolicyService {

    @Value("${keycloak.realm}")
    private String realmName;

    @Value("${keycloak.clients.backend.id}")
    private String backendClientName;

    private final KeycloakConnectService keycloakService;
    private final KeycloakRealmService keycloakRealmService;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public boolean policyExists(ClientResource clientResource, String policyName) {
        return findByName(clientResource, policyName).isPresent();
    }

    @Override
    public void createPolicy(PolicyRepresentation policy, String realmName, String client) {

        Keycloak keycloak = keycloakService.getKeyCloakAdminCli();
        ClientResource clientResource = keycloakRealmService
                .getClientResource(keycloak, realmName, client)
                .orElse(null);

        if (clientResource != null) {
            clientResource.authorization().policies().create(policy);
        }
        keycloak.tokenManager().logout();
        keycloak.close();

    }

    @Override
    public Optional<PolicyRepresentation> findByName(ClientResource clientResource, String policyName) {
        PolicyRepresentation policy = clientResource.authorization().policies().findByName(policyName);
        return policy != null ? Optional.of(policy) : Optional.empty();
    }

    @Override
    public void deletePolicy(ClientResource clientResource, String policyName) {
        if (!policyExists(clientResource, policyName)) {
            return;
        }
        Optional<PolicyRepresentation> policy = findByName(clientResource, policyName);
        if (policy.isPresent()) {
            String policyId = policy.get().getId();
            clientResource.authorization().policies().policy(policyId).remove();
        }

    }

    @Override
    public List<PolicyRepresentation> mapPolicyRequestListToPolicyRepresentation(String realmName, String clientName,
            KeycloakResourceRequestDto request) {

        List<PolicyRepresentation> policies = new ArrayList<>();

        request.getScopes().forEach(scope -> {
            PolicyRepresentation policy = new PolicyRepresentation();
            String policyName = request.getName() + " " + scope.getName();
            policy.setName(policyName);
            policy.setType("role");
            policy.setDescription(scope.getDisplayName() + " " + request.getDisplayName());

            Map<String, String> policyConfigMap = setupPolicyConfig(realmName,
                    clientName,
                    scope.getRoles());

            policy.setConfig(policyConfigMap);

            policies.add(policy);

        });
        return policies;

    }

    @Override
    public Map<String, String> setupPolicyConfig(String realm, String client, List<KeycloakRoleRequestDto> roles) {
        Keycloak keycloak = keycloakService.getKeyCloakAdminCli();
        ClientResource clientResource = keycloakRealmService
                .getClientResource(keycloak, realm, client)
                .orElse(null);

        List<String> scopeRoles = roles.stream().map(KeycloakRoleRequestDto::getName).toList();
        Map<String, String> policyConfig = new HashMap<>();
        if (clientResource != null) {
            List<String> rolesIds = clientResource.roles().list().stream()
                    .filter(role -> scopeRoles.contains(role.getName()))
                    .map(RoleRepresentation::getId).collect(Collectors.toList());

            policyConfig = getPolicyConfig(rolesIds);
            keycloak.tokenManager().logout();
            keycloak.close();
        }
        return policyConfig;
    }

    public Map<String, String> getPolicyConfig(List<String> roleIds) {
        Map<String, String> policyConfig = new HashMap<>();

        List<PolicyRoleConfig> roles = new ArrayList<>();

        for (String roleId : roleIds) {
            roles.add(new PolicyRoleConfig(roleId, false));
        }

        try {
            String rolesJson = OBJECT_MAPPER.writeValueAsString(roles);
            policyConfig.put("roles", rolesJson);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return policyConfig;
    }

    @Override
    public List<PolicyDto> getAllPolicies() {
        Keycloak keycloak = keycloakService.getKeyCloakAdminCli();
        try {
            return keycloakRealmService.getClientResource(keycloak, realmName, backendClientName)
                    .map(clientResource -> mapPolicyRepresentationsToPolicyDtos(getAllPolicies(clientResource)))
                    .orElse(new ArrayList<>());
        } finally {
            keycloak.tokenManager().logout();
            keycloak.close();
        }
    }

    @Override
    public List<PolicyDto> mapPolicyRepresentationsToPolicyDtos(List<PolicyRepresentation> policyRepresentations) {
        List<PolicyDto> policyDtos = new ArrayList<>();

        // Filter only for policies with type "role"
        List<PolicyRepresentation> rolePolicies = policyRepresentations.stream()
                .filter(policy -> "role".equals(policy.getType()))
                .collect(Collectors.toList());

        for (PolicyRepresentation policy : rolePolicies) {
            PolicyDto dto = new PolicyDto();
            dto.setId(policy.getId());
            dto.setName(policy.getName());
            dto.setDescription(policy.getDescription());
            dto.setType(policy.getType());

            // Process roles using the approach from getAllPoliciesO
            List<PolicyRoleDto> roleDtos = new ArrayList<>();

            if (policy.getConfig() != null) {
                roleDtos = extractRoleReferences(policy.getConfig());

            }

            dto.setRoles(roleDtos);
            policyDtos.add(dto);
        }

        return policyDtos;
    }

    private List<PolicyRoleDto> extractRoleReferences(Map<String, String> roleConfig) {
        ObjectMapper objectMapper = new ObjectMapper();

        List<PolicyRoleDto> roleDtos = new ArrayList<>();

        Object rolesObj = roleConfig.get("roles");
        if (rolesObj instanceof String) {
            String rolesJson = (String) rolesObj;
            try {
                List<Map<String, Object>> rolesList = objectMapper.readValue(rolesJson,
                        new TypeReference<List<Map<String, Object>>>() {
                        });

                // Convert each role map to PolicyRoleDto
                for (Map<String, Object> role : rolesList) {
                    String id = (String) role.get("id");
                    Boolean required = false;
                    if (role.containsKey("required")) {
                        required = (Boolean) role.get("required");
                    }

                    if (id != null) {
                        roleDtos.add(new PolicyRoleDto(id, required));
                    }
                }

            } catch (Exception e) {
                // Just create empty list - no error logging
            }
        }
        return roleDtos;

    }

    @Override
    public List<PolicyRepresentation> getAllPolicies(ClientResource clientResource) {
        return clientResource.authorization().policies().policies();

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
     * @param policiesIds the list of policy IDs
     * @param roleName    the name of the role to assign
     * @return true if all assignments succeed, false otherwise
     */
    public boolean assignRoleToPolicies(RolePoliciesRequestDto requestDto) {
        List<String> policiesIds = requestDto.getPoliciesIds();
        String roleName = requestDto.getRoleName();
        boolean allSuccess = true;
        Keycloak keycloak = keycloakService.getKeyCloakAdminCli();
        try {
            Optional<ClientResource> clientResourceOpt = keycloakRealmService.getClientResource(keycloak, realmName,
                    backendClientName);
            if (clientResourceOpt.isEmpty()) {
                log.error("Client resource not found");
                return false;
            }
            ClientResource clientResource = clientResourceOpt.get();
            List<PolicyRepresentation> allPolicies = clientResource.authorization().policies().policies();

            if (policiesIds == null || policiesIds.isEmpty()) {
                // Detach role from all policies
                for (PolicyRepresentation policy : allPolicies) {
                    if ("role".equals(policy.getType())) {
                        boolean result = detachRoleFromPolicy(policy.getId(), roleName);
                        if (!result)
                            allSuccess = false;
                    }
                }
                return allSuccess;
            }

            // For each policy, if it exists in policiesIds, assign; if not, detach
            for (PolicyRepresentation policy : allPolicies) {
                if ("role".equals(policy.getType())) {
                    if (policiesIds.contains(policy.getId())) {
                        boolean result = assignRoleToPolicy(policy.getId(), roleName);
                        if (!result)
                            allSuccess = false;
                    } else {
                        boolean result = detachRoleFromPolicy(policy.getId(), roleName);
                        if (!result)
                            allSuccess = false;
                    }
                }
            }
            return allSuccess;
        } finally {
            keycloak.tokenManager().logout();
            keycloak.close();
        }
    }

    private boolean detachRoleFromPolicy(String policyId, String roleName) {
        Keycloak keycloak = keycloakService.getKeyCloakAdminCli();
        boolean result = false;
        try {
            result = keycloakRealmService.getClientResource(keycloak, realmName, backendClientName)
                    .map(clientResource -> {
                        try {
                            var policy = clientResource.authorization().policies().policy(policyId).toRepresentation();
                            if (policy == null || !"role".equals(policy.getType())) {
                                log.error("Policy '{}' is null or not of type 'role' for detach", policyId);
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
                                log.error("Role '{}' not found for detach", roleName);
                                return false;
                            }
                            // Remove role by ID
                            boolean removed = rolesList.removeIf(r -> roleRep.getId().equals(r.get("id")));
                            if (!removed) {
                                log.info("Role '{}' not present in policy '{}', nothing to detach.", roleName,
                                        policyId);
                                return true;
                            }
                            String updatedRolesJson = objectMapper.writeValueAsString(rolesList);
                            config.put("roles", updatedRolesJson);
                            return assignRoleToPolicyConfig(policyId, config);
                        } catch (Exception e) {
                            log.error("Error detaching role from policy '{}': {}", policyId, e.getMessage(), e);
                            return false;
                        }
                    }).orElse(false);
        } finally {
            keycloak.tokenManager().logout();
            keycloak.close();
        }
        return result;
    }

    private boolean assignRoleToPolicy(String policyId, String roleName) {
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
                                    new com.fasterxml.jackson.core.type.TypeReference<List<Map<String, Object>>>() {
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
                            // Add new role
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
