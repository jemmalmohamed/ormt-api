package ma.org.ormt.modules.indicateurs.donnee.dtos;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.Dto;
import ma.org.ormt.modules.indicateurs.valeurdimension.dtos.ValeurDimensionDto;

@Setter
@Getter
@Schema(name = "DonneeIndicateurDto")
@JsonIgnoreProperties(value = { "donneeIndicateur.id" }, allowGetters = true)
@AllArgsConstructor
@NoArgsConstructor
public class DonneeIndicateurDto extends Dto {

    private String valeur;

    private List<ValeurDimensionDto> valeurDimensions;

}