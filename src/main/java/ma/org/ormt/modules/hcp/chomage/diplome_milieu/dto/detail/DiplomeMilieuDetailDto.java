package ma.org.ormt.modules.hcp.chomage.diplome_milieu.dto.detail;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.modules.hcp.chomage.diplome_milieu.dto.DiplomeMilieuDto;

@Setter
@Getter
@Schema(name = "DiplomeMilieuDetailDto")
@RequiredArgsConstructor
@JsonIgnoreProperties(value = { "DiplomeMilieuDto.id" }, allowGetters = true)
public class DiplomeMilieuDetailDto extends DiplomeMilieuDto {

}
