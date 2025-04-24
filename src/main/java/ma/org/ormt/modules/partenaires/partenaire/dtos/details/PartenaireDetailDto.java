package ma.org.ormt.modules.partenaires.partenaire.dtos.details;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.modules.partenaires.partenaire.dtos.PartenaireDto;

@Setter
@Getter
@Schema(name = "PartenaireDetailDto")
@RequiredArgsConstructor
@JsonIgnoreProperties(value = { "partenaire.id" }, allowGetters = true)
public class PartenaireDetailDto extends PartenaireDto {

}
