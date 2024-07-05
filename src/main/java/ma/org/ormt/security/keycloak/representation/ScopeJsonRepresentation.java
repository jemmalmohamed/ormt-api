package ma.org.ormt.security.keycloak.representation;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ScopeJsonRepresentation {

    String name;
    List<RoleJsonConfig> roles;
}