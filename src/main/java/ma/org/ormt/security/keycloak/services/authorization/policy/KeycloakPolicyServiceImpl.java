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
import ma.org.ormt.security.keycloak.dto.request.KeycloakRoleRequestDto;
import ma.org.ormt.security.keycloak.dto.request.KeycloakScopeRequestDto;
import ma.org.ormt.security.keycloak.services.KeycloakConnectService;
import ma.org.ormt.security.keycloak.services.realm.KeycloakRealmService;

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
            List<KeycloakScopeRequestDto> scopes, String prefixName) {
        List<PolicyRepresentation> policies = new ArrayList<>();

        scopes.forEach(scope -> {
            PolicyRepresentation policy = new PolicyRepresentation();
            String policyName = prefixName + " " + scope.getName();
            policy.setName(policyName);
            policy.setType("role");
            policy.setDescription(policyName + " policy");

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
    public List<Map<String, Object>> getAllPolicies(ClientResource clientResource) {
        List<Map<String, Object>> policies = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
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
                    .map(clientResource -> assignRoleToPolicy(clientResource, policyId, roleName))
                    .orElse(false);
        } finally {
            keycloak.tokenManager().logout();
            keycloak.close();
        }
        return result;
    }

    private boolean assignRoleToPolicy(ClientResource clientResource, String policyId, String roleName) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'assignRoleToPolicy'");
    }

}
