package ma.org.ormt.security.keycloak.services.authorization.policy;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.representations.idm.authorization.PolicyRepresentation;

import ma.org.ormt.security.keycloak.dto.request.KeycloakResourceRequestDto;
import ma.org.ormt.security.keycloak.dto.request.KeycloakRoleRequestDto;
import ma.org.ormt.security.policy.dto.PolicyDto;
import ma.org.ormt.security.policy.dto.RolePoliciesRequestDto;

public interface KeycloakPolicyService {

    boolean policyExists(ClientResource clientResource, String policyName);

    Optional<PolicyRepresentation> findByName(ClientResource clientResource, String policyName);

    void createPolicy(PolicyRepresentation policy, String realmName, String client);

    void deletePolicy(ClientResource clientResource, String policyName);

    Map<String, String> getPolicyConfig(List<String> roleIds);

    Map<String, String> setupPolicyConfig(String realm, String client, List<KeycloakRoleRequestDto> roles);

    List<PolicyRepresentation> mapPolicyRequestListToPolicyRepresentation(String realmName, String clientName,
            KeycloakResourceRequestDto request);

    List<PolicyDto> mapPolicyRepresentationsToPolicyDtos(List<PolicyRepresentation> policyRepresentations);

    List<PolicyDto> getAllPolicies();

    List<PolicyRepresentation> getAllPolicies(ClientResource clientResource);

    boolean assignRoleToPolicies(RolePoliciesRequestDto requestDto);
}
