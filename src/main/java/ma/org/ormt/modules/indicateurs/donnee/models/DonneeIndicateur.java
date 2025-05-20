package ma.org.ormt.modules.indicateurs.donnee.models;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ma.org.ormt.core.commun.base.entity.BaseEntity;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;
import ma.org.ormt.modules.indicateurs.valeurdimension.models.ValeurDimension;

import java.util.List;

@Setter
@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "donnee_indicateur")
public class DonneeIndicateur extends BaseEntity {

    private String valeur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_indicateur")
    private Indicateur indicateur;

    @OneToMany(mappedBy = "donneeIndicateur", fetch = FetchType.EAGER)
    private List<ValeurDimension> valeurDimensions;

}