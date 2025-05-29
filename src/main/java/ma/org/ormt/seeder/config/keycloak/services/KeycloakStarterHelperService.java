package ma.org.ormt.seeder.config.keycloak.services;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.representations.idm.authorization.DecisionStrategy;
import org.keycloak.representations.idm.authorization.PolicyRepresentation;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;
import org.keycloak.representations.idm.authorization.ScopePermissionRepresentation;
import org.keycloak.representations.idm.authorization.ScopeRepresentation;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ma.org.ormt.security.keycloak.dto.request.KeycloakResourceRequestDto;
import ma.org.ormt.security.keycloak.dto.request.KeycloakScopeRequestDto;
import ma.org.ormt.security.keycloak.services.KeycloakConnectService;
import ma.org.ormt.security.keycloak.services.authorization.permission.KeycloakPermissionService;
import ma.org.ormt.security.keycloak.services.authorization.policy.KeycloakPolicyService;
import ma.org.ormt.security.keycloak.services.authorization.resource.KeycloakResourceService;
import ma.org.ormt.security.keycloak.services.authorization.scope.KeycloakScopeService;
import ma.org.ormt.security.keycloak.services.realm.KeycloakRealmService;

@Log4j2
@Service
@RequiredArgsConstructor
public class KeycloakStarterHelperService {

    private final KeycloakResourceService keycloakResourceService;

    private final KeycloakRealmService keycloakRealmService;

    private final KeycloakScopeService keycloakScopeService;

    private final KeycloakConnectService keycloakService;

    private final KeycloakPolicyService keycloakPolicyService;

    private final KeycloakPermissionService keycloakPermissionService;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public void createOrUpdateResource(ClientResource clientResource, KeycloakResourceRequestDto jsonResource) {
        ResourceRepresentation resource = mapToResourceRepresentationWithScopes(clientResource, jsonResource);
        boolean resourceExists = keycloakResourceService.resourceExists(clientResource, resource.getName());
        if (resourceExists) {
            keycloakResourceService.deleteResource(clientResource, resource.getName());
        }
        keycloakResourceService.createResource(clientResource, resource);
    }

    public void createPoliciesAndPermissionList(String realmName, String backendClientName,
            List<KeycloakResourceRequestDto> resources) {

        List<PolicyRepresentation> policies = createPoliciesList(realmName, backendClientName, resources);
        createPoliciesBatch(realmName, backendClientName, policies);

        try (Keycloak keycloak = keycloakService.getKeyCloakAdminCli()) {
            ClientResource clientResource = keycloakRealmService
                    .getClientResource(keycloak, realmName, backendClientName)
                    .orElse(null);

            List<ScopePermissionRepresentation> permissions = resources.stream()
                    .flatMap(resource -> createScopePermissionRepresentations(clientResource, resource)
                            .stream())
                    .collect(Collectors.toList());

            keycloak.tokenManager().logout();
            // keycloak.close();
            createPermissionsBatch(realmName, backendClientName, permissions);
        }
    }

    public List<PolicyRepresentation> createPoliciesList(String realmName, String backendClientName,
            List<KeycloakResourceRequestDto> resourceRequestList) {
        return resourceRequestList.stream()
                .flatMap(resourceRequest -> keycloakPolicyService
                        .mapPolicyRequestListToPolicyRepresentation(realmName, backendClientName,
                                resourceRequest)
                        .stream())
                .collect(Collectors.toList());

    }

    private ResourceRepresentation mapToResourceRepresentationWithScopes(ClientResource clientResource,
            KeycloakResourceRequestDto resourceRequest) {

        ResourceRepresentation resource = new ResourceRepresentation();

        resource.setName(resourceRequest.getName());
        resource.setDisplayName(resourceRequest.getDisplayName());
        resource.setUris(resourceRequest.getUris());

        List<ScopeRepresentation> scopes = keycloakScopeService.getScopes(clientResource);
        if (scopes.isEmpty()) {
            List<ScopeRepresentation> scopesJson = new ArrayList<>();
            resourceRequest.getScopes().forEach(scope -> {
                ScopeRepresentation scopeRepresentation = new ScopeRepresentation();
                scopeRepresentation.setName(scope.getName());
                scopeRepresentation.setDisplayName(scope.getDisplayName());
                scopesJson.add(scopeRepresentation);
            });
            scopes = scopesJson;
        }
        for (ScopeRepresentation scopeRepresentation : scopes) {
            resource.addScope(scopeRepresentation);
        }
        return resource;
    }

    private List<ScopePermissionRepresentation> createScopePermissionRepresentations(ClientResource clientResource,
            KeycloakResourceRequestDto resourceJson) {
        List<ScopePermissionRepresentation> permissions = new ArrayList<>();

        keycloakResourceService.findResourceByName(clientResource, resourceJson.getName()).ifPresentOrElse(resource -> {
            String resourceId = resource.getId();
            for (KeycloakScopeRequestDto scope : resourceJson.getScopes()) {
                keycloakScopeService.findByName(clientResource, scope.getName()).ifPresentOrElse(scopeRep -> {
                    String scopeId = scopeRep.getId();
                    String policyName = resourceJson.getName() + " " + scope.getName();
                    keycloakPolicyService.findByName(clientResource, policyName).ifPresentOrElse(policy -> {
                        String policyId = policy.getId();
                        String permissionName = resourceJson.getName() + ":" + scope.getName();
                        String permissionDescription = permissionName + " permission";
                        ScopePermissionRepresentation permission = new ScopePermissionRepresentation();
                        permission.setName(permissionName);
                        permission.setDescription(permissionDescription);
                        permission.addResource(resourceId);
                        permission.addScope(scopeId);
                        permission.addPolicy(policyId);
                        permission.setDecisionStrategy(DecisionStrategy.UNANIMOUS);
                        permissions.add(permission);
                    }, () -> log.warn("Policy '{}' not found for resource '{}'", policyName, resourceJson.getName()));
                }, () -> log.warn("Scope '{}' not found for resource '{}'", scope.getName(), resourceJson.getName()));
            }
        }, () -> log.warn("Resource '{}' not found for permission creation", resourceJson.getName()));
        return permissions;
    }

    private void createPoliciesBatch(String realmName, String client, List<PolicyRepresentation> policies) {
        for (PolicyRepresentation policy : policies) {
            keycloakPolicyService.createPolicy(policy, realmName, client);
        }
    }

    private void createPermissionsBatch(String realmName, String client,
            List<ScopePermissionRepresentation> permissions) {
        executeInBatch(permissions,
                permission -> keycloakPermissionService.createPermission(permission, realmName, client));
    }

    private <T> void executeInBatch(List<T> items, Consumer<T> action) {
        ExecutorService executor = Executors.newFixedThreadPool(30);
        try {
            for (T item : items) {
                executor.submit(() -> action.accept(item));
            }
            executor.shutdown();
            executor.awaitTermination(Long.MAX_VALUE, java.util.concurrent.TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Batch execution interrupted", e);
        }
    }

    public List<KeycloakResourceRequestDto> loadResourceDtos(List<String> resourceNames) {
        List<KeycloakResourceRequestDto> resourcesJson = new ArrayList<>();
        for (String resourceName : resourceNames) {
            resourcesJson.addAll(loadResourceDto(resourceName));
        }
        return resourcesJson;
    }

    public List<KeycloakResourceRequestDto> loadResourceDto(String resourceNameFile) {
        String resourcePath = "init-data/authentication/resources/" + resourceNameFile + ".resource.json";
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                log.warn("### KEYCLOAK: Resource file '{}' not found", resourcePath);
                return Collections.emptyList();
            }
            return OBJECT_MAPPER.readValue(inputStream, new TypeReference<List<KeycloakResourceRequestDto>>() {
            });
        } catch (Exception e) {
            log.error("### KEYCLOAK: Error reading resource file '{}'", resourcePath, e);
            return Collections.emptyList();
        }
    }

}
