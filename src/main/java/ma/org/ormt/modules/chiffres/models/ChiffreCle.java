package ma.org.ormt.modules.chiffres.models;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import ma.org.ormt.modules.chiffres.association.domaine.ChiffreCleDomaine;
import ma.org.ormt.modules.chiffres.models.enums.KpiEvolutionMode;
import ma.org.ormt.modules.chiffres.models.enums.KpiFormatType;
import ma.org.ormt.modules.chiffres.models.enums.KpiModeSource;
import ma.org.ormt.modules.indicateurs.donnee.models.DonneeIndicateur;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;

@Setter
@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "chiffre_cle")
public class ChiffreCle extends BaseEntity {

    @Column(unique = true)
    private String libelle;

    private String unite;

    private String valeur;

    private String description;

    private Boolean afficherDate;

    @Column(name = "afficher_description")
    private Boolean afficherDescription;

    private boolean actif;

    private String accessType;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode_source", nullable = false)
    private KpiModeSource modeSource;

    @Enumerated(EnumType.STRING)
    @Column(name = "format_type", nullable = false)
    private KpiFormatType formatType;

    @Column(name = "prefix_label")
    private String prefixLabel;

    @Column(name = "suffix_label")
    private String suffixLabel;

    @Enumerated(EnumType.STRING)
    @Column(name = "evolution_mode", nullable = false)
    private KpiEvolutionMode evolutionMode;

    @Column(name = "metadata_json", columnDefinition = "TEXT")
    private String metadataJson;

    @Column(name = "style_json", columnDefinition = "TEXT")
    private String styleJson;

    @ManyToOne
    @JoinColumn(name = "donnee_indicateur_id")
    private DonneeIndicateur donneeIndicateur;

    @ManyToOne
    @JoinColumn(name = "indicateur_id")
    private Indicateur indicateur;

    @OneToMany(mappedBy = "chiffreCle", fetch = FetchType.LAZY)
    private List<ChiffreCleDomaine> chiffrecleDomaines;

    // @OneToMany(mappedBy = "chiffreCle", fetch = FetchType.LAZY)
    // private List<ChiffreCleDomaine> chiffrecleEspaces;

}
