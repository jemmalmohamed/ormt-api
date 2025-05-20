package ma.org.ormt.modules.indicateurs.valeurdimension.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ma.org.ormt.core.commun.base.entity.BaseEntity;
import ma.org.ormt.modules.indicateurs.donnee.models.DonneeIndicateur;
import ma.org.ormt.modules.indicateurs.dimension.models.Dimension;

@Setter
@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "valeur_dimension")
public class ValeurDimension extends BaseEntity {

    private String valeur;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_dimension")
    private Dimension dimension;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_donnee_indicateur")
    private DonneeIndicateur donneeIndicateur;

}