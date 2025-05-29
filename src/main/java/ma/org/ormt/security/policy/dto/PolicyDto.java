package ma.org.ormt.security.policy.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PolicyDto {

    private String id;
    private String name;
    private String description;
    private String type;

    private List<PolicyRoleDto> roles;

}
