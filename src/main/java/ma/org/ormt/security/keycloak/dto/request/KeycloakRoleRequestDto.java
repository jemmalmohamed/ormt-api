package ma.org.ormt.security.keycloak.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class KeycloakRoleRequestDto {
    String name;
    Boolean required;
}