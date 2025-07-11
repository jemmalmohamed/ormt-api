package ma.org.ormt.modules.indicateurs.graphe.type.models;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ma.org.ormt.core.commun.base.entity.BaseEntity;
// import ma.org.ormt.modules.indicateurs.graphe.models.ChartConfiguration;
// import ma.org.ormt.modules.indicateurs.graphe.models.ChartMappingRule;
import ma.org.ormt.modules.indicateurs.graphe.configuration.models.GrapheConfiguration;
import ma.org.ormt.modules.indicateurs.graphe.mapping.models.GrapheMappingRule;

@Setter
@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "graphe_type")
public class GrapheType extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private String nom;

    private String description;

    @Column(name = "chart_js_type", nullable = false)
    private String chartJsType;

    @Column(name = "min_dimensions")
    private Integer minDimensions;

    @Column(name = "max_dimensions")
    private Integer maxDimensions;

    @Column(name = "requires_temporal")
    private Boolean requiresTemporal;

    private Boolean actif;

    // Relations will be added after creating other entities

    @OneToMany(mappedBy = "grapheType", fetch = FetchType.LAZY)
    private List<GrapheMappingRule> mappingRules;

    @OneToMany(mappedBy = "grapheType", fetch = FetchType.LAZY)
    private List<GrapheConfiguration> configurations;
}
