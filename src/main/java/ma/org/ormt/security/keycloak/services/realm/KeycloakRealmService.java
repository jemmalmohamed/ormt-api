
package ma.org.ormt.security.keycloak.services.realm;

import java.util.Optional;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.RealmResource;

public interface KeycloakRealmService {

    boolean realmExists(Keycloak keycloakInstance, String realmName);

    RealmResource getRealmResource(Keycloak keycloakInstance, String realmName);

    RealmResource createRealm(Keycloak keycloakInstance, String realmName);

    void deleteRealm(Keycloak keycloakInstance, String realmName);

    Optional<ClientResource> getClientResource(Keycloak keycloakInstance, String realm, String client);

}