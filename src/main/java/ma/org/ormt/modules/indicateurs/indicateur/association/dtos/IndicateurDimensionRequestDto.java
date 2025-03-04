package ma.org.ormt.modules.indicateurs.indicateur.association.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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
public class IndicateurDimensionRequestDto extends Dto {

    @NotBlank(message = "Ce champ est requis.")
    private Boolean principale;

    @NotBlank(message = "Ce champ est requis.")
    private Boolean temporelle;

    @NotBlank(message = "Ce champ est requis.")
    private DimensionSummaryDto dimension;

    @NotBlank(message = "Ce champ est requis.")
    private IndicateurSummaryDto indicateur;

}
