package ma.org.ormt.modules.indicateurs.dimension.models;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ma.org.ormt.core.commun.base.entity.BaseEntity;
import ma.org.ormt.modules.indicateurs.indicateur.association.dimension.models.IndicateurDimension;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;

@Setter
@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "dimension")
public class Dimension extends BaseEntity {

    @Column(unique = true)
    private String nom;

    private String type;

    private String description;

    private String libelle;

    @OneToMany(mappedBy = "dimension")
    private List<IndicateurDimension> indicateurDimensions;

    @Transient
    private List<Indicateur> getIndicateurs() {
        return indicateurDimensions.stream()
                .map(IndicateurDimension::getIndicateur)
                .collect(Collectors.toList());
    }

}