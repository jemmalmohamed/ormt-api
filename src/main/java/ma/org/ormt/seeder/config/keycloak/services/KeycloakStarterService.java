package ma.org.ormt.seeder.config.keycloak.services;

import java.util.ArrayList;
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

import lombok.RequiredArgsConstructor;
import ma.org.ormt.security.keycloak.config.KeycloakService;
import ma.org.ormt.security.keycloak.representation.ResourceJsonRepresentation;
import ma.org.ormt.security.keycloak.representation.ScopeJsonRepresentation;
import ma.org.ormt.security.keycloak.services.authorization.permission.KeycloakPermissionService;
import ma.org.ormt.security.keycloak.services.authorization.policy.KeycloakPolicyService;
import ma.org.ormt.security.keycloak.services.authorization.resource.KeycloakResourceService;
import ma.org.ormt.security.keycloak.services.authorization.scope.KeycloakScopeService;
import ma.org.ormt.security.keycloak.services.realm.KeycloakRealmService;

@Service
@RequiredArgsConstructor
public class KeycloakStarterService {

    private final KeycloakResourceService keycloakResourceService;

    private final KeycloakRealmService keycloakRealmService;

    private final KeycloakScopeService keycloakScopeService;

    private final KeycloakService keycloakService;

    private final KeycloakPolicyService keycloakPolicyService;

    private final KeycloakPermissionService keycloakPermissionService;

    public void createOrUpdateResource(ClientResource clientResource, ResourceJsonRepresentation jsonResource) {
        ResourceRepresentation resource = mapToResourceRepresentation(clientResource, jsonResource);
        boolean resourceExists = keycloakResourceService.resourceExists(clientResource, resource.getName());
        if (resourceExists) {
            keycloakResourceService.deleteResource(clientResource, resource.getName());
        }

        keycloakResourceService.createResource(clientResource, resource);

    }

    public void createPoliciesAndPermissionList(String realmName, String backendClientName,
            List<ResourceJsonRepresentation> resources) {

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
            List<ResourceJsonRepresentation> resources) {
        return resources.stream()
                .flatMap(resource -> keycloakPolicyService
                        .createPolicyRepresentations(realmName, backendClientName, resource)
                        .stream())
                .collect(Collectors.toList());

    }

    private ResourceRepresentation mapToResourceRepresentation(ClientResource clientResource,
            ResourceJsonRepresentation resourceJson) {

        ResourceRepresentation resource = new ResourceRepresentation();

        resource.setName(resourceJson.getName());
        resource.setDisplayName(resourceJson.getDisplayName());
        resource.setUris(resourceJson.getUris());
        List<ScopeRepresentation> scopes = getScopes(clientResource);
        if (scopes.isEmpty()) {
            List<ScopeRepresentation> scopesJson = new ArrayList<>();
            resourceJson.getScopes().forEach(scope -> {
                ScopeRepresentation scopeRepresentation = new ScopeRepresentation();
                scopeRepresentation.setName(scope.getName());
                scopesJson.add(scopeRepresentation);
            });
            scopes = scopesJson;
        }
        for (ScopeRepresentation scopeRepresentation : scopes) {
            resource.addScope(scopeRepresentation);
        }
        return resource;
    }

    private List<ScopeRepresentation> getScopes(ClientResource clientResource) {
        return clientResource.authorization().scopes().scopes();
    }

    private List<ScopePermissionRepresentation> createScopePermissionRepresentations(ClientResource clientResource,
            ResourceJsonRepresentation resourceJson) {
        List<ScopePermissionRepresentation> permissions = new ArrayList<>();

        String resourceId = keycloakResourceService.findResourceByName(clientResource,
                resourceJson.getName())
                .orElse(null)
                .getId();

        // Collect all permissions first
        for (ScopeJsonRepresentation scope : resourceJson.getScopes()) {

            String scopeId = keycloakScopeService.findByName(clientResource, scope.getName())
                    .orElse(null)
                    .getId();

            String policyId = keycloakPolicyService.findByName(clientResource,
                    resourceJson.getName() + " " + scope.getName()).orElse(null).getId();

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
        }
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
        } finally {
            executor.shutdown();
        }
    }

}
