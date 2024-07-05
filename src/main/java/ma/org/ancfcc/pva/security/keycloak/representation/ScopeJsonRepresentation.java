package ma.org.ancfcc.pva.security.keycloak.representation;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ScopeJsonRepresentation {

    String name;
    List<RoleJsonConfig> roles;
}