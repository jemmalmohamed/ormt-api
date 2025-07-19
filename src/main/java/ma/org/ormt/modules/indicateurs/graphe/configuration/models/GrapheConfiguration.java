package ma.org.ormt.modules.indicateurs.graphe.configuration.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ma.org.ormt.core.commun.base.entity.BaseEntity;
import ma.org.ormt.modules.indicateurs.graphe.type.models.GrapheType;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;

@Setter
@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "graphe_configuration")
public class GrapheConfiguration extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "indicateur_id", nullable = false)
    private Indicateur indicateur;

    @ManyToOne
    @JoinColumn(name = "graphe_type_id", nullable = false)
    private GrapheType grapheType;

    private String nom;

    @Column(name = "dimension_mapping_json", columnDefinition = "TEXT", nullable = false)
    private String dimensionMappingJson;

    @Column(name = "chart_options_json", columnDefinition = "TEXT")
    private String chartOptionsJson;

    @Column(name = "is_default")
    private Boolean isDefault;

}
