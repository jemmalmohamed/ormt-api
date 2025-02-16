package ma.org.ormt.modules.indicateur.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.Dto;

@Builder
@Setter
@Getter
@Schema(name = "IndicateurDimensionRequestDto")
@RequiredArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(value = { "indicateurDimension.id" }, allowGetters = true)
public class IndicateurDimensionRequestDto extends Dto {

    @NotBlank(message = "Ce champ est requis.")
    private String nom;

    @NotBlank(message = "Ce champ est requis.")
    private String type;

    private String description;
}