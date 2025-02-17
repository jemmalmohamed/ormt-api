package ma.org.ormt.modules.indicateurs.dimension.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.BaseDto;
import ma.org.ormt.core.commun.base.dto.Dto;

@Setter
@Getter
@Schema(name = "Dimension")
@JsonIgnoreProperties(value = { "dimension.id" }, allowGetters = true)
@AllArgsConstructor
@NoArgsConstructor
public class DimensionDto extends Dto {

    private String nom;

    private String type;

    private String description;

    private String libelle;

}
