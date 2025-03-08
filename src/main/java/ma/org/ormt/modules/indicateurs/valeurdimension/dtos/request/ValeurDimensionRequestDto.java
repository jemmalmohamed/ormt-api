package ma.org.ormt.modules.indicateurs.valeurdimension.dtos.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.Dto;
import ma.org.ormt.modules.indicateurs.dimension.dtos.DimensionDto;

@Setter
@Getter
@Schema(name = "ValeurDimensionRequest")
@RequiredArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(value = { "ValeurDimensionRequest.id" }, allowGetters = true)
public class ValeurDimensionRequestDto extends Dto {

    @NotBlank(message = "Ce champ est requis.")
    private String valeur;

    // @NotNull(message = "Le sous-domaine est requis.")
    // private Long idDimension;

    @NotNull(message = "Le sous-domaine est requis.")
    private DimensionDto dimension;
}