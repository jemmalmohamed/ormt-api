package ma.org.ormt.security.keycloak.services.authorization.policy;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleConfig {
    private String id;

    private boolean required;

    public RoleConfig(String id, boolean required) {
        this.id = id;

        this.required = required;
    }
}
