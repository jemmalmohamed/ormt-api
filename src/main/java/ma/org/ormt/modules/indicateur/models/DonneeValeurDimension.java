package ma.org.ormt.modules.indicateur.models;

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

@Setter
@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "donnee_valeur_dimension")
public class DonneeValeurDimension extends BaseEntity {

    private String valeur;

    @ManyToOne
    @JoinColumn(name = "id_donnee")
    private DonneeIndicateur donnee;

    @ManyToOne
    @JoinColumn(name = "id_indicateur_dimension")
    private IndicateurDimension indicateurDimension;
}
