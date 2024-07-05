package ma.org.ormt.security.keycloak.services.authorization.policy;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.representations.idm.authorization.PolicyRepresentation;

import ma.org.ormt.security.keycloak.representation.ResourceJsonRepresentation;
import ma.org.ormt.security.keycloak.representation.RoleJsonConfig;

public interface KeycloakPolicyService {

    boolean policyExists(ClientResource clientResource, String policyName);

    Optional<PolicyRepresentation> findByName(ClientResource clientResource, String policyName);

    void createPolicy(PolicyRepresentation policy, String realmName, String client);

    void deletePolicy(ClientResource clientResource, String policyName);

    Map<String, String> getPolicyConfig(List<String> roleIds);

    Map<String, String> setupPolicyConfig(String realm, String client, List<RoleJsonConfig> roles);

    List<PolicyRepresentation> createPolicyRepresentations(String realmName, String clientName,
            ResourceJsonRepresentation resourceJson);
}
