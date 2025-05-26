package ma.org.ormt.security.keycloak.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class KeycloakRoleRequestDto {
    String name;
    Boolean required;
}