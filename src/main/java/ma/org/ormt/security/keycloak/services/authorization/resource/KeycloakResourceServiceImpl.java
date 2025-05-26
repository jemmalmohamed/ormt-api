package ma.org.ormt.security.keycloak.services.authorization.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.ResourceResource;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;
import org.keycloak.representations.idm.authorization.ScopeRepresentation;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ma.org.ormt.security.authorization.dto.ResourceDto;
import ma.org.ormt.security.keycloak.services.KeycloakConnectService;

@Service
@RequiredArgsConstructor
public class KeycloakResourceServiceImpl implements KeycloakResourceService {

    private final KeycloakConnectService keycloakService;

    @Override
    public boolean resourceExists(ClientResource clientResource, String resourceName) {
        try {
            return clientResource.authorization().resources().resources().stream()
                    .filter(resource -> resource.getName().equals(resourceName)).findFirst().isPresent();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Optional<ResourceRepresentation> findResourceByName(ClientResource clientResource, String resourceName) {
        boolean resourceExists = resourceExists(clientResource, resourceName);
        if (!resourceExists) {
            return Optional.empty();
        }
        return Optional.of(clientResource.authorization().resources().findByName(resourceName).get(0));
    }

    @Override
    public ResourceResource createResource(ClientResource clientResource,
            ResourceRepresentation resourceRepresentation) {
        if (resourceExists(clientResource, resourceRepresentation.getName())) {
            return null;
        } else {
            clientResource.authorization().resources().create(resourceRepresentation);

            Optional<ResourceRepresentation> optionalResource = findResourceByName(clientResource,
                    resourceRepresentation.getName());

            if (optionalResource.isPresent()) {
                String resourceId = optionalResource.get().getId();
                return clientResource.authorization().resources().resource(resourceId);
            } else {
                // Handle the case where the resource is not found after creation
                throw new IllegalStateException("Resource not found after creation");
            }
        }
    }

    @Override
    public void deleteResource(ClientResource clientResource, String resourceName) {
        if (!resourceExists(clientResource, resourceName)) {
            return;
        }

        Optional<ResourceRepresentation> optionalResource = findResourceByName(clientResource, resourceName);

        if (optionalResource.isPresent()) {
            String resourceId = optionalResource.get().getId();
            clientResource.authorization().resources().resource(resourceId).remove();
        } else {
            // Handle the case where the resource is not found
            throw new IllegalStateException("Resource not found for deletion");
        }

    }

}
