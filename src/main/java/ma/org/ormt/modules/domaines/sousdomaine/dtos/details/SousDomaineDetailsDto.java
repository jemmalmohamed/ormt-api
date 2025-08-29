package ma.org.ormt.modules.domaines.sousdomaine.dtos.details;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.modules.domaines.sousdomaine.dtos.SousDomaineDto;
import ma.org.ormt.modules.indicateurs.indicateur.dtos.sousdomaine.IndicateurSousDomaineDetailDto;

@Setter
@Getter
@Schema(name = "SousDomaineDetailsDto")
@RequiredArgsConstructor
@JsonIgnoreProperties(value = { "sousdomaine.id" }, allowGetters = true)
public class SousDomaineDetailsDto extends SousDomaineDto {

    private List<IndicateurSousDomaineDetailDto> indicateurs;

}
