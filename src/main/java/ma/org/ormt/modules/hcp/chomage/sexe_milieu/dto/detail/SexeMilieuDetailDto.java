package ma.org.ormt.modules.hcp.chomage.sexe_milieu.dto.detail;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.modules.hcp.chomage.sexe_milieu.dto.SexeMilieuDto;

@Setter
@Getter
@Schema(name = "SexeMilieuDetailDto")
@RequiredArgsConstructor
@JsonIgnoreProperties(value = { "SexeMilieuDto.id" }, allowGetters = true)
public class SexeMilieuDetailDto extends SexeMilieuDto {

}
