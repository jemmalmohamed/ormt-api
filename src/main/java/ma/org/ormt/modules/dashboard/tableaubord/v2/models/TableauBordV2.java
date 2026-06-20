package ma.org.ormt.modules.dashboard.tableaubord.v2.models;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import ma.org.ormt.modules.dashboard.tableaubord.v2.enums.TableauBordV2Status;

@Setter
@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tableau_bord_v2")
public class TableauBordV2 extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String nom;

    @Column(nullable = false)
    private String titre;

    @Column(name = "sous_titre")
    private String sousTitre;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String source;

    @Column(name = "periode_label")
    private String periodeLabel;

    @lombok.Builder.Default
    private Boolean actif = true;

    @lombok.Builder.Default
    @Enumerated(EnumType.STRING)
    private TableauBordV2Status status = TableauBordV2Status.DRAFT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categorie_id")
    private TableauBordV2Categorie categorie;

    @Column(name = "theme_json", columnDefinition = "TEXT")
    private String themeJson;

    @Column(name = "settings_json", columnDefinition = "TEXT")
    private String settingsJson;

    @lombok.Builder.Default
    @OneToMany(mappedBy = "dashboard", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ordre ASC, id ASC")
    private List<TableauBordV2Widget> widgets = new ArrayList<>();
}
