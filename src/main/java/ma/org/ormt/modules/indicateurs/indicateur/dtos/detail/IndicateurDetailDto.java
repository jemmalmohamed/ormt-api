package ma.org.ormt.modules.indicateurs.indicateur.dtos.detail;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.modules.indicateurs.donnee.dtos.DonneeIndicateurDto;
import ma.org.ormt.modules.indicateurs.indicateur.association.dimension.dtos.IndicateurDimensionDto;
import ma.org.ormt.modules.indicateurs.indicateur.dtos.IndicateurDto;

@Setter
@Getter
@Schema(name = "IndicateurDetailDto")
@RequiredArgsConstructor
@JsonIgnoreProperties(value = { "indicateur.id" }, allowGetters = true)
public class IndicateurDetailDto extends IndicateurDto {

    private List<IndicateurDimensionDto> indicateurDimensions;

    private List<DonneeIndicateurDto> donnees;
}