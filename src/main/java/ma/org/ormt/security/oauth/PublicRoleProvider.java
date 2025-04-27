package ma.org.ormt.security.oauth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.authorization.PolicyRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ma.org.ormt.security.keycloak.config.KeycloakService;

@Component
@RequiredArgsConstructor
@Log4j2
public class PublicRoleProvider {

    private final KeycloakService keycloakService;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.clients.backend.id}")
    private String clientId;

    // Cache for public permissions to avoid frequent Keycloak calls
    private Collection<GrantedAuthority> cachedPublicAuthorities;

    @PostConstruct
    public void init() {
        // Initialize the cache at startup
        refreshPublicAuthoritiesCache();
    }

    /**
     * Creates an anonymous authentication with public role permissions.
     * 
     * @return An AnonymousAuthenticationToken with public permissions
     */
    public Authentication createPublicAuthentication() {
        Collection<GrantedAuthority> authorities = getPublicAuthorities();
        log.debug("Creating public authentication with authorities: {}", authorities);
        return new AnonymousAuthenticationToken("anonymousKey", "anonymousUser", authorities);
    }

    /**
     * Returns the list of authorities granted to public/anonymous users
     * by fetching them from Keycloak.
     * Will fetch fresh authorities on each call to ensure permissions are always
     * up-to-date.
     * 
     * @return Collection of public role authorities
     */
    public Collection<GrantedAuthority> getPublicAuthorities() {
        // Always refresh the cache to ensure up-to-date permissions
        refreshPublicAuthoritiesCache();
        return cachedPublicAuthorities;
    }

    /**
     * Refreshes the cached public authorities by fetching from Keycloak
     */
    public void refreshPublicAuthoritiesCache() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        log.info("Refreshing public authorities cache");

        try (Keycloak keycloak = keycloakService.getKeyCloakAdminCli()) {
            // Add the basic ROLE_PUBLIC authority
            authorities.add(new SimpleGrantedAuthority("ROLE_PUBLIC"));

            // Get client ID (not client representation ID)
            String clientUniqueId = null;

            try {
                // Get the actual client ID (not clientId which is the client name)
                List<ClientRepresentation> clients = keycloak.realm(realm).clients().findByClientId(clientId);
                if (!clients.isEmpty()) {
                    clientUniqueId = clients.get(0).getId();
                    log.info("Found client ID: {} for client: {}", clientUniqueId, clientId);
                } else {
                    log.warn("Client not found: {}", clientId);
                }
            } catch (Exception e) {
                log.error("Error getting client ID: {}", e.getMessage(), e);
            }

            if (clientUniqueId != null) {
                try {
                    log.info("Attempting to get public role permissions directly");

                    // Direct access to the public role permissions
                    RoleResource publicRoleResource = keycloak
                            .realm(realm)
                            .clients().get(clientUniqueId)
                            .roles().get("public");

                    // Get the role representation first to make sure it exists
                    RoleRepresentation publicRole = publicRoleResource.toRepresentation();
                    log.info("Found public role: {} with ID: {}", publicRole.getName(), publicRole.getId());

                    // Direct approach using the Management API to get permissions

                    // Get all permission policies to find ones that apply to this role
                    List<PolicyRepresentation> allPolicies = keycloak
                            .realm(realm)
                            .clients().get(clientUniqueId)
                            .authorization()
                            .policies()
                            .policies();

                    // Instead of using getPermissions() which causes 501 Not Implemented,
                    // we'll use a more compatible approach to get role permissions
                    try {
                        // Get the composite roles (roles that include the public role)

                        // Get role attributes that might contain permission info
                        Map<String, List<String>> attributes = publicRole.getAttributes();
                        if (attributes != null && !attributes.isEmpty()) {
                            log.info("Public role has {} attributes", attributes.size());

                            // Process attributes to extract permissions if they're stored there
                            for (Map.Entry<String, List<String>> entry : attributes.entrySet()) {
                                String key = entry.getKey();
                                List<String> values = entry.getValue();

                                if (key.startsWith("permission_") && values != null && !values.isEmpty()) {
                                    for (String value : values) {
                                        authorities.add(new SimpleGrantedAuthority(value));
                                        log.debug("Added permission from attribute: {}", value);
                                    }
                                }
                            }
                        }

                        // Check if there are any resource-scopes directly associated with this role
                        // Convert policies to proper permissions format instead of using POLICY_ prefix
                        for (PolicyRepresentation policy : allPolicies) {
                            if (policy.getType().equals("role") &&
                                    policy.getConfig() != null &&
                                    policy.getConfig().get("roles") != null &&
                                    policy.getConfig().get("roles").contains(publicRole.getId())) {

                                String policyName = policy.getName();
                                // Parse the policy name to extract resource and action
                                // Assuming policy names are in format "RESOURCE ACTION" (e.g., "AUTH LIST")
                                String[] parts = policyName.split(" ");
                                if (parts.length >= 2) {
                                    String resource = parts[0].toLowerCase();
                                    String action = parts[1].toLowerCase();

                                    // Convert to permission format: resource:action
                                    String permission = resource + ":" + action;
                                    authorities.add(new SimpleGrantedAuthority(permission));
                                    log.debug("Added permission: {} from policy: {}", permission, policyName);
                                } else {
                                    // Fallback if policy name doesn't follow expected format
                                    authorities.add(
                                            new SimpleGrantedAuthority(policyName.toLowerCase().replace(" ", ":")));
                                    log.debug("Added fallback permission from policy: {}", policyName);
                                }
                            }
                        }
                    } catch (Exception ex) {
                        log.error("Error processing public role permissions: {}", ex.getMessage(), ex);
                    }
                } catch (Exception e) {
                    log.error("Error getting permissions directly: {}", e.getMessage(), e);
                    // Fall back to the previous approach
                }
            }

            log.info("Loaded {} public authorities from Keycloak", authorities.size());
            keycloak.tokenManager().logout();
        } catch (Exception e) {
            log.error("Error fetching public authorities from Keycloak: {}", e.getMessage(), e);
            // Fallback to default permissions if there's an error
            if (!authorities.contains(new SimpleGrantedAuthority("ROLE_PUBLIC"))) {
                authorities.add(new SimpleGrantedAuthority("ROLE_PUBLIC"));
            }
        }

        log.info("Public authorities after processing: {}", authorities);
        this.cachedPublicAuthorities = authorities;
    }

}