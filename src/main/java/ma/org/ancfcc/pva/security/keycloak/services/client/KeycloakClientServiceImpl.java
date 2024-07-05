
package ma.org.ancfcc.pva.security.keycloak.services.client;

import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.springframework.stereotype.Service;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KeycloakClientServiceImpl implements KeycloakClientService {

    @Override
    public ClientResource getClientResource(RealmResource realmResource, String clientName) {

        ClientRepresentation clientRepresentation = realmResource.clients().findByClientId(clientName).get(0);
        return realmResource.clients().get(clientRepresentation.getId());

    }

    public boolean clientExists(RealmResource realmResource, String clientId) {
        return !realmResource.clients().findByClientId(clientId).isEmpty();
    }

    @Override
    public ClientResource createClient(RealmResource realmResource, ClientRepresentation clientRepresentation) {
        if (clientExists(realmResource, clientRepresentation.getClientId())) {
            return null;
        }

        try (Response response = realmResource.clients().create(clientRepresentation)) {
            if (response.getStatus() != 201) {
                return null;
            }
        }

        return this.getClientResource(realmResource, clientRepresentation.getClientId());
    }

    @Override
    public ClientResource enableClientAuthorization(ClientResource clientResource) {

        ClientRepresentation clientRepresentation = clientResource.toRepresentation();
        clientRepresentation.setPublicClient(false);
        clientRepresentation.setServiceAccountsEnabled(true);
        clientRepresentation.setAuthorizationServicesEnabled(true);
        clientResource.update(clientRepresentation);

        return clientResource;
    }

    @Override
    public void deleteClient(RealmResource realmResource, String clientId) {
        if (!clientExists(realmResource, clientId)) {
            return;
        }

        ClientRepresentation clientRepresentation = realmResource.clients().findByClientId(clientId).get(0);
        realmResource.clients().get(clientRepresentation.getId()).remove();

    }

    @Override
    public String getClientSecret(RealmResource realmResource, String clientId) {

        ClientRepresentation clientRepresentation = realmResource.clients().findByClientId(clientId).get(0);
        return realmResource.clients().get(clientRepresentation.getId()).getSecret().getValue();
    }

}