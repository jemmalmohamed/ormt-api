package ma.org.ormt.modules.sousdomaine.dtos.details;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.modules.sousdomaine.dtos.SousDomaineDto;

@Setter
@Getter
@Schema(name = "DomaineDetailDto")
@RequiredArgsConstructor
@JsonIgnoreProperties(value = { "sousdomaine.id" }, allowGetters = true)
public class SousDomaineDetailsDto extends SousDomaineDto {

}
