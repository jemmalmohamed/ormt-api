package ma.org.ormt.security.keycloak.dto.request;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class KeycloakScopeRequestDto {
    String name;
    String displayName;
    List<KeycloakRoleRequestDto> roles;
}