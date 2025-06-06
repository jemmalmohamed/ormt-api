package ma.org.ormt.modules.indicateurs.indicateur.association.dimension.models;

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
import ma.org.ormt.modules.indicateurs.dimension.models.Dimension;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;

@Setter
@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "indicateur_dimension")
public class IndicateurDimension extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "id_indicateur")
    private Indicateur indicateur;

    @ManyToOne
    @JoinColumn(name = "id_dimension")
    private Dimension dimension;

    private Boolean principale;

    private Boolean temporelle;
}
