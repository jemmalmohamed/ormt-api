package ma.org.ormt.security.roleacces.dtos.details;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.security.roleacces.dtos.RoleAccesDto;

@Setter
@Getter
@Schema(name = "roleAccesDetailDto")
@RequiredArgsConstructor
@JsonIgnoreProperties(value = { "roleAcces.id" }, allowGetters = true)
public class RoleAccesDetailDto extends RoleAccesDto {

}
