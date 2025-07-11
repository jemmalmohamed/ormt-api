package ma.org.ormt.modules.indicateurs.graphe.mapping.models;

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

@Setter
@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "graphe_mapping_rule")
public class GrapheMappingRule extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "graphe_type_id", nullable = false)
    private GrapheType grapheType;

    @Column(name = "mapping_key", nullable = false)
    private String mappingKey;

    @Column(name = "is_required")
    private Boolean isRequired;

    @Column(name = "is_forbidden")
    private Boolean isForbidden;

    @Column(name = "must_be_temporal")
    private Boolean mustBeTemporal;

    @Column(name = "must_be_geographic")
    private Boolean mustBeGeographic;

    @Column(name = "max_values")
    private Integer maxValues;

    private String description;
}
