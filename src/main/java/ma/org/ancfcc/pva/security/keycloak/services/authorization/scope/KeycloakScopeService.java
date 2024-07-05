package ma.org.ancfcc.pva.security.keycloak.services.authorization.scope;

import java.util.List;
import java.util.Optional;

import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.representations.idm.authorization.ScopeRepresentation;

public interface KeycloakScopeService {

    boolean scopeExists(ClientResource clientResource, String scopeName);

    Optional<ScopeRepresentation> findByName(ClientResource clientResource, String scopeName);

    ScopeRepresentation createScope(ClientResource clientResource, ScopeRepresentation scopeRepresentation);

    void deleteScope(ClientResource clientResource, String scopeName);

    List<ScopeRepresentation> getScopes(ClientResource clientResource);
}
