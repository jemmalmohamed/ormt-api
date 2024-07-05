package ma.org.ormt.security.keycloak.services.authorization.resource;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.ResourceResource;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;
import org.keycloak.representations.idm.authorization.ScopeRepresentation;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import ma.org.ormt.security.keycloak.representation.ResourceJsonRepresentation;

@Service
@RequiredArgsConstructor
public class KeycloakResourceServiceImpl implements KeycloakResourceService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public boolean resourceExists(ClientResource clientResource, String resourceName) {
        try {
            return !clientResource.authorization().resources().resources().stream()
                    .filter(resource -> resource.getName().equals(resourceName)).findFirst().isEmpty();
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

    @Override
    public ScopeRepresentation createScope(ClientResource clientResource, ScopeRepresentation scopeRepresentation,
            boolean suppress) {

        clientResource.authorization().scopes().create(scopeRepresentation);
        return scopeRepresentation;
    }

    @Override
    public List<ResourceJsonRepresentation> getJsonResourceRepresentations(String resourceNameFile) {
        try {
            InputStream inputStream = getClass().getClassLoader()
                    .getResourceAsStream("init-data/authentication/resources/" + resourceNameFile + ".json");
            return OBJECT_MAPPER.readValue(inputStream, new TypeReference<List<ResourceJsonRepresentation>>() {
            });
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

}
