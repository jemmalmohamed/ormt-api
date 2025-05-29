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
public class RolePoliciesRequestDto {

    List<String> policiesIds;

    String roleName;

}
