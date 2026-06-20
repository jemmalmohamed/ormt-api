package ma.org.ormt.modules.dashboard.tableaubord.v2.models;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ma.org.ormt.core.commun.base.entity.BaseEntity;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.models.TBDomaine;

@Setter
@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tableau_bord_v2_categorie")
public class TableauBordV2Categorie extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String nom;

    @Column(nullable = false)
    private String libelle;

    @Column(columnDefinition = "TEXT")
    private String description;

    @lombok.Builder.Default
    private Integer ordre = 0;

    @lombok.Builder.Default
    private Boolean actif = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tb_domaine_id")
    private TBDomaine tbDomaine;

    @lombok.Builder.Default
    @OneToMany(mappedBy = "categorie", fetch = FetchType.LAZY)
    @OrderBy("titre ASC")
    private List<TableauBordV2> dashboards = new ArrayList<>();
}
