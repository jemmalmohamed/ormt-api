package ma.org.ormt.security.keycloak.services.authorization.policy;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PolicyRoleConfig {
    private String id;
    private boolean required;
}
