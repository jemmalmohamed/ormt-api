package ma.org.ormt.security.users.users.dtos;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Schema(name = "User")
@JsonIgnoreProperties(value = { "user.id" }, allowGetters = true)
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private boolean enabled;
    private Map<String, List<String>> clientRoles;
    private List<String> roles;

}
