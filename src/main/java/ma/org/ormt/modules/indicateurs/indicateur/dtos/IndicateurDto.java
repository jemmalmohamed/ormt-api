package ma.org.ormt.modules.indicateurs.indicateur.dtos;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.BaseDto;
import ma.org.ormt.core.commun.base.dto.Dto;
import ma.org.ormt.modules.indicateurs.dimension.dtos.summary.DimensionSummaryDto;
import ma.org.ormt.modules.indicateurs.donnee.dtos.DonneeIndicateurDto;

@Setter
@Getter
@Schema(name = "Indicateur")
@JsonIgnoreProperties(value = { "indicateur.id" }, allowGetters = true)
@AllArgsConstructor
@NoArgsConstructor
public class IndicateurDto extends BaseDto {

    private String nom;

    private String source;

    private String regleCalcul;

    private String unite;

    private String typeTb;

    private List<DimensionSummaryDto> dimensions;

    private List<DonneeIndicateurDto> donnees;

    private Dto sousDomaine;
}