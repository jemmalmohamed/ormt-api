package ma.org.ormt.modules.analytics.category.models;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
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
import ma.org.ormt.modules.analytics.domain.models.DomaineAnalytique;
import ma.org.ormt.modules.dashboard.tbd.models.TbdDashboard;

@Setter
@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "categorie_analytique")
public class CategorieAnalytique extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "domaine_analytique_id", nullable = false)
    private DomaineAnalytique domaineAnalytique;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tbd_dashboard_id")
    private TbdDashboard tbdDashboard;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String libelle;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String slug;

    @lombok.Builder.Default
    private Integer ordre = 0;

    @lombok.Builder.Default
    private Boolean actif = true;

    @lombok.Builder.Default
    @OneToMany(mappedBy = "categorieAnalytique", fetch = FetchType.LAZY)
    private List<CategorieAnalytiqueSection> sections = new ArrayList<>();
}
