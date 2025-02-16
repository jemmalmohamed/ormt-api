package ma.org.ormt.security.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AuthorisationDto {

    List<RoleDto> roles;
    List<PermissionDto> permissions;

}
