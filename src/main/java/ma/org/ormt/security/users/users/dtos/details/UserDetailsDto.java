package ma.org.ormt.security.users.users.dtos.details;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.security.users.users.dtos.UserDto;

@Setter
@Getter
@Schema(name = "userDetailsDto")
@RequiredArgsConstructor
@JsonIgnoreProperties(value = { "user.id" }, allowGetters = true)
public class UserDetailsDto extends UserDto {

}