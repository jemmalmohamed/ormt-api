package ma.org.ormt.security.keycloak.services.authorization.resource;

import java.util.List;
import java.util.Optional;

import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.ResourceResource;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;

import ma.org.ormt.security.authorization.dto.ResourceDto;

public interface KeycloakResourceService {

    boolean resourceExists(ClientResource clientResource, String resourceName);

    Optional<ResourceRepresentation> findResourceByName(ClientResource clientResource, String resourceName);

    ResourceResource createResource(ClientResource clientResource, ResourceRepresentation resourceRepresentation);

    void deleteResource(ClientResource clientResource, String resourceName);

}