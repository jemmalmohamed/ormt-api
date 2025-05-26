package ma.org.ormt.security.keycloak.dto.request;

import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO for transferring Keycloak resource data from configuration files or
 * frontend/backend requests.
 * Used to create or update Keycloak resources via the admin API.
 */
@Setter
@Getter
public class KeycloakResourceRequestDto {
    private String name;
    private String displayName;
    private Set<String> uris;
    private List<KeycloakScopeRequestDto> scopes;
}