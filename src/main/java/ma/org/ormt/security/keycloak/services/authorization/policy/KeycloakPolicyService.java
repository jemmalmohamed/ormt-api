package ma.org.ormt.security.keycloak.services.authorization.policy;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.representations.idm.authorization.PolicyRepresentation;

import ma.org.ormt.security.keycloak.dto.request.KeycloakRoleRequestDto;
import ma.org.ormt.security.keycloak.dto.request.KeycloakScopeRequestDto;

public interface KeycloakPolicyService {

    boolean policyExists(ClientResource clientResource, String policyName);

    Optional<PolicyRepresentation> findByName(ClientResource clientResource, String policyName);

    void createPolicy(PolicyRepresentation policy, String realmName, String client);

    void deletePolicy(ClientResource clientResource, String policyName);

    Map<String, String> getPolicyConfig(List<String> roleIds);

    Map<String, String> setupPolicyConfig(String realm, String client, List<KeycloakRoleRequestDto> roles);

    List<PolicyRepresentation> mapPolicyRequestListToPolicyRepresentation(String realmName, String clientName,
            List<KeycloakScopeRequestDto> scopes, String prefixName);

    List<Map<String, Object>> getAllPolicies(ClientResource clientResource);

    boolean assignRoleToPolicy(String policyId, String roleName);

    boolean assignRoleToPolicies(List<String> policyIds, String roleName);
}
