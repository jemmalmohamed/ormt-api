package ma.org.ancfcc.pva.security.keycloak.representation;

import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResourceJsonRepresentation {

    String name;
    String displayName;
    Set<String> uris;
    List<ScopeJsonRepresentation> scopes;

}