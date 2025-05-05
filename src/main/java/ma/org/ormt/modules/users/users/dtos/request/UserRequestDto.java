package ma.org.ormt.modules.users.users.dtos.request;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.validators.groups.OnCreate;

@Setter
@Getter
@Schema(name = "UserRequest")
@RequiredArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(value = { "user.id" }, allowGetters = true)
public class UserRequestDto {

    private String id;

    @NotBlank(message = "Ce champ est requis.")
    private String username;

    @NotBlank(message = "Ce champ est requis.")
    private String firstName;

    @NotBlank(message = "Ce champ est requis.")
    private String lastName;

    @Email(message = "L'email doit être valide.")
    private String email;

    @NotNull(message = "Ce champ est requis.")
    private Boolean enabled;

    @NotBlank(message = "Ce champ est requis.", groups = { OnCreate.class })
    private String password;

    // @NotBlank(message = "Ce champ est requis.")
    private Map<String, List<String>> clientRoles;

}