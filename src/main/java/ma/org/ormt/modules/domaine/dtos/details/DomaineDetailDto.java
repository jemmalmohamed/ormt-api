package ma.org.ormt.modules.domaine.dtos.details;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.modules.domaine.dtos.DomaineDto;

@Setter
@Getter
@Schema(name = "DomaineDetailDto")
@RequiredArgsConstructor
@JsonIgnoreProperties(value = { "domaine.id" }, allowGetters = true)
public class DomaineDetailDto extends DomaineDto {

}
