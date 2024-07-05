
package ma.org.ormt.security.keycloak.services.client;

import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.ClientRepresentation;

public interface KeycloakClientService {

    boolean clientExists(RealmResource realmResource, String clientId);

    ClientResource getClientResource(RealmResource realmResource, String clientId);

    ClientResource createClient(RealmResource realmResource, ClientRepresentation clientRepresentation);

    ClientResource enableClientAuthorization(ClientResource clientResource);

    void deleteClient(RealmResource realmResource, String clientId);

    String getClientSecret(RealmResource realmResource, String clientId);

}