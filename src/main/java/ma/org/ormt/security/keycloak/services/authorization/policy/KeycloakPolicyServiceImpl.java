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
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import ma.org.ormt.security.keycloak.config.KeycloakService;
import ma.org.ormt.security.keycloak.representation.ResourceJsonRepresentation;
import ma.org.ormt.security.keycloak.representation.RoleJsonConfig;
import ma.org.ormt.security.keycloak.services.realm.KeycloakRealmService;

@Service
@RequiredArgsConstructor
public class KeycloakPolicyServiceImpl implements KeycloakPolicyService {

    private final KeycloakService keycloakService;
    private final KeycloakRealmService keycloakRealmService;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public boolean policyExists(ClientResource clientResource, String policyName) {
        return clientResource.authorization().policies().findByName(policyName) != null;
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
        return clientResource.authorization().policies().findByName(policyName) != null
                ? Optional.of(clientResource.authorization().policies().findByName(policyName))
                : Optional.empty();
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
    public List<PolicyRepresentation> createPolicyRepresentations(String realmName, String clientName,
            ResourceJsonRepresentation resourceJson) {
        List<PolicyRepresentation> policies = new ArrayList<>();
        resourceJson.getScopes().forEach(scope -> {
            PolicyRepresentation policy = new PolicyRepresentation();
            String policyName = resourceJson.getName() + " " + scope.getName();
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
    public Map<String, String> setupPolicyConfig(String realm, String client, List<RoleJsonConfig> roles) {
        Keycloak keycloak = keycloakService.getKeyCloakAdminCli();
        ClientResource clientResource = keycloakRealmService
                .getClientResource(keycloak, realm, client)
                .orElse(null);

        List<String> scopeRoles = roles.stream().map(RoleJsonConfig::getName).toList();
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

        List<RoleConfig> roles = new ArrayList<>();

        for (String roleId : roleIds) {
            roles.add(new RoleConfig(roleId, false));
        }

        try {
            String rolesJson = OBJECT_MAPPER.writeValueAsString(roles);
            policyConfig.put("roles", rolesJson);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return policyConfig;
    }

}
