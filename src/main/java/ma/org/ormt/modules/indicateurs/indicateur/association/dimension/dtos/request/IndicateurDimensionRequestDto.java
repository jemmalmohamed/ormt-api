package ma.org.ormt.modules.indicateurs.indicateur.association.dimension.dtos.request;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.Dto;
import ma.org.ormt.modules.indicateurs.dimension.dtos.DimensionDto;
import ma.org.ormt.modules.indicateurs.indicateur.dtos.IndicateurDto;

@Setter
@Getter
@Schema(name = "IndicateurDimension")
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class IndicateurDimensionRequestDto extends Dto {

    @NotNull(message = "Ce champ est requis.")
    private Boolean principale;

    @NotNull(message = "Ce champ est requis.")
    private Boolean temporelle;
    @NotNull(message = "Ce champ est requis.")
    private DimensionDto dimension;

    @NotNull(message = "Ce champ est requis.")
    private IndicateurDto indicateur;

}
