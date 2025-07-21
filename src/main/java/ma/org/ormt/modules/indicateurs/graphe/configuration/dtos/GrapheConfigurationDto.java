package ma.org.ormt.modules.indicateurs.graphe.configuration.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.Dto;
import ma.org.ormt.modules.indicateurs.graphe.type.dtos.GrapheTypeDto;

@Setter
@Getter
@Schema(name = "GrapheConfiguration")
@JsonIgnoreProperties(value = { "grapheConfiguration.id" }, allowGetters = true)
@AllArgsConstructor
@NoArgsConstructor
public class GrapheConfigurationDto extends Dto {

    private Dto indicateur;

    private GrapheTypeDto grapheType;

    private String nom;

    private String dimensionMappingJson;

    private String chartOptionsJson;

    private Boolean isDefault;

}
