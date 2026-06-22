package ma.org.ormt.modules.analytics.category.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@Table(name = "categorie_analytique_section")
public class CategorieAnalytiqueSection extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categorie_analytique_id", nullable = false)
    private CategorieAnalytique categorieAnalytique;

    @Column(nullable = false, length = 50)
    private String type;

    private String titre;

    @Column(name = "content_json", columnDefinition = "TEXT")
    private String contentJson;

    @lombok.Builder.Default
    private Integer ordre = 0;

    @lombok.Builder.Default
    private Boolean actif = true;
}
