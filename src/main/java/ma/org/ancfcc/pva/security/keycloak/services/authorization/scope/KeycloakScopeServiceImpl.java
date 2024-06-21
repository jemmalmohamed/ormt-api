package ma.org.ancfcc.pva.security.keycloak.services.authorization.scope;

import java.util.List;
import java.util.Optional;

import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.representations.idm.authorization.ScopeRepresentation;
import org.springframework.stereotype.Service;

@Service
public class KeycloakScopeServiceImpl implements KeycloakScopeService {

    @Override
    public List<ScopeRepresentation> getScopes(ClientResource clientResource) {
        return clientResource.authorization().scopes().scopes();
    }

    @Override
    public boolean scopeExists(ClientResource clientResource, String scopeName) {
        return clientResource.authorization().scopes().findByName(scopeName) != null;
    }

    @Override
    public ScopeRepresentation createScope(ClientResource clientResource, ScopeRepresentation scopeRepresentation) {
        if (scopeExists(clientResource, scopeRepresentation.getName())) {
            return null;
        }
        clientResource.authorization().scopes().create(scopeRepresentation);
        return scopeRepresentation;
    };

    @Override
    public Optional<ScopeRepresentation> findByName(ClientResource clientResource, String scopeName) {
        return clientResource.authorization().scopes().findByName(scopeName) != null
                ? Optional.of(clientResource.authorization().scopes().findByName(scopeName))
                : Optional.empty();
    }

    @Override
    public void deleteScope(ClientResource clientResource, String scopeName) {
        if (!scopeExists(clientResource, scopeName)) {
            return;
        }
        String scopeId = findByName(clientResource, scopeName).get().getId();
        clientResource.authorization().scopes().scope(scopeId).remove();
    };
}
