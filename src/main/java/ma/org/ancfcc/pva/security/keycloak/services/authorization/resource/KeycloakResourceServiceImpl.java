package ma.org.ancfcc.pva.security.keycloak.services.authorization.resource;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import jakarta.ws.rs.NotFoundException;

import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.ResourceResource;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;
import org.keycloak.representations.idm.authorization.ScopeRepresentation;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import ma.org.ancfcc.pva.security.keycloak.representation.ResourceJsonRepresentation;

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
        }
        clientResource.authorization().resources().create(resourceRepresentation);

        String resourceId = findResourceByName(clientResource, resourceRepresentation.getName()).get().getId();

        return clientResource.authorization().resources().resource(resourceId);
    }

    @Override
    public void deleteResource(ClientResource clientResource, String resourceName) {
        if (!resourceExists(clientResource, resourceName)) {
            return;
        }
        String resourceId = findResourceByName(clientResource, resourceName).get().getId();
        clientResource.authorization().resources().resource(resourceId).remove();

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
