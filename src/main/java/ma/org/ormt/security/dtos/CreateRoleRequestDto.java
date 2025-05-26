package ma.org.ormt.security.dtos;

import java.util.List;

import lombok.Data;

@Data
public class CreateRoleRequestDto {
    private String roleName;
    private String description;
    private List<String> permissions;
}
