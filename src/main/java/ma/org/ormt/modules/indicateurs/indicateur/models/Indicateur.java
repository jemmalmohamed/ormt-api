package ma.org.ormt.modules.indicateurs.indicateur.models;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ma.org.ormt.core.commun.base.entity.BaseEntity;
import ma.org.ormt.modules.domaines.sousdomaine.models.SousDomaine;
import ma.org.ormt.modules.indicateurs.dimension.models.Dimension;
import ma.org.ormt.modules.indicateurs.donnee.models.DonneeIndicateur;

@Setter
@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "indicateur")
public class Indicateur extends BaseEntity {

    private String nom;

    private String description;

    private String abreviation;

    private String categorie;

    private String typeTb;

    private String unite;

    private String source;

    private String regleCalcul;

    @OneToMany(mappedBy = "indicateur", fetch = FetchType.EAGER)
    private List<IndicateurDimension> indicateurDimensions;

    @Transient
    public List<Dimension> getDimensions() {
        return indicateurDimensions.stream()
                .map(IndicateurDimension::getDimension)
                .collect(Collectors.toList());
    }

    @Builder.Default
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "indicateur_sous_domaine", joinColumns = @JoinColumn(name = "id_indicateur", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "id_sous_domaine", referencedColumnName = "id"))
    private List<SousDomaine> sousDomaines = new ArrayList<>();

    @OneToMany(mappedBy = "indicateur", fetch = FetchType.LAZY)
    private List<DonneeIndicateur> donnees;

}