package ma.org.ormt.security.keycloak.dto.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class KeycloakScopeRequestDto {
    String name;
    List<KeycloakRoleRequestDto> roles;
}