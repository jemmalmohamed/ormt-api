package ma.org.ormt.modules.indicateur.models;

import java.util.List;

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
import ma.org.ormt.modules.sousdomaine.SousDomaine;

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

    private String uniteCalcul;

    private String source;

    private String regleCalcul;

    @OneToMany(mappedBy = "indicateur")
    private List<IndicateurDimension> dimensions;

    @OneToMany(mappedBy = "indicateur")
    private List<DonneeIndicateur> donnees;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sous_domaine")
    private SousDomaine sousDomaine;

}