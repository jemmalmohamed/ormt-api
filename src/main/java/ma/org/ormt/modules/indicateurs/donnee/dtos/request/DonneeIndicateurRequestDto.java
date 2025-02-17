package ma.org.ormt.modules.indicateurs.donnee.dtos.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.Dto;
import ma.org.ormt.modules.indicateurs.valeurdimension.dtos.request.ValeurDimensionRequestDto;

@Setter
@Getter
@Schema(name = "DonneeIndicateurRequest")
@RequiredArgsConstructor
@AllArgsConstructor

@JsonIgnoreProperties(value = { "DonneeIndicateur.id" }, allowGetters = true)
public class DonneeIndicateurRequestDto extends Dto {

    @NotBlank(message = "Ce champ est requis.")
    private String valeur;

    private List<ValeurDimensionRequestDto> valeurDimensions;

}