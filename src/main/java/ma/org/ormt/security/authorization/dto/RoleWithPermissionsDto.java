package ma.org.ormt.security.authorization.dto;

import java.util.List;

import lombok.Data;

@Data
public class RoleWithPermissionsDto {
    private String roleName;
    private String description;
    private List<String> permissions;
}
