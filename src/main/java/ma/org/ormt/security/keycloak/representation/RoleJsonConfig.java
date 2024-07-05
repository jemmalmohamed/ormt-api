package ma.org.ormt.security.keycloak.representation;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RoleJsonConfig {
    String name;
    Boolean required;
}