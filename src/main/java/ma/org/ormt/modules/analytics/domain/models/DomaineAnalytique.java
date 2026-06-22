package ma.org.ormt.modules.analytics.domain.models;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ma.org.ormt.core.commun.base.entity.BaseEntity;
import ma.org.ormt.modules.analytics.association.espace.EspaceDomaineAnalytique;
import ma.org.ormt.modules.analytics.association.tbgroup.TbGroupDomaineAnalytique;
import ma.org.ormt.modules.analytics.category.models.CategorieAnalytique;

@Setter
@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "domaine_analytique")
public class DomaineAnalytique extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String nom;

    @Column(nullable = false)
    private String titre;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String apropos;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(unique = true, nullable = false)
    private String slug;

    @Column(name = "source_theme_key")
    private String sourceThemeKey;

    @Column(name = "metadata_json", columnDefinition = "TEXT")
    private String metadataJson;

    @lombok.Builder.Default
    private Boolean actif = true;

    @lombok.Builder.Default
    @OneToMany(mappedBy = "domaineAnalytique", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CategorieAnalytique> categories = new ArrayList<>();

    @lombok.Builder.Default
    @OneToMany(mappedBy = "domaineAnalytique", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DomaineAnalytiqueSection> sections = new ArrayList<>();

    @lombok.Builder.Default
    @OneToMany(mappedBy = "domaineAnalytique", fetch = FetchType.LAZY)
    private List<EspaceDomaineAnalytique> espaceDomainesAnalytiques = new ArrayList<>();

    @lombok.Builder.Default
    @OneToMany(mappedBy = "domaineAnalytique", fetch = FetchType.LAZY)
    private List<TbGroupDomaineAnalytique> tbGroupDomainesAnalytiques = new ArrayList<>();
}
