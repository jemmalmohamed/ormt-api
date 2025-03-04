package ma.org.ormt.modules.indicateurs.indicateur.association.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.Dto;
import ma.org.ormt.modules.indicateurs.dimension.dtos.summary.DimensionSummaryDto;
import ma.org.ormt.modules.indicateurs.indicateur.dtos.summary.IndicateurSummaryDto;

@Setter
@Getter
@Schema(name = "IndicateurDimension")
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class IndicateurDimensionDto extends Dto {

    private Boolean principale;

    private Boolean temporelle;

    private DimensionSummaryDto dimension;

    private IndicateurSummaryDto indicateur;

}
