package ma.org.ormt.modules.indicateurs.graphe.type.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.Dto;

@Setter
@Getter
@Schema(name = "GrapheType")
@JsonIgnoreProperties(value = { "graphetype.id" }, allowGetters = true)
@AllArgsConstructor
@NoArgsConstructor
public class GrapheTypeDto extends Dto {

    private String code;

    private String nom;

    private String description;

    private String chartJsType;

    private Boolean actif;

    // Relations will be added after creating other entities

    // @OneToMany(mappedBy = "grapheType", fetch = FetchType.LAZY)
    // private List<ChartMappingRule> mappingRules;

    // @OneToMany(mappedBy = "grapheType", fetch = FetchType.LAZY)
    // private List<ChartConfiguration> configurations;
}
