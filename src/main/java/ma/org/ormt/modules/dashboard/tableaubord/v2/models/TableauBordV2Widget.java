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
import ma.org.ormt.modules.chiffres.models.ChiffreCle;
import ma.org.ormt.modules.dashboard.tableaubord.v2.enums.TableauBordV2DataSourceType;
import ma.org.ormt.modules.dashboard.tableaubord.v2.enums.TableauBordV2WidgetType;
import ma.org.ormt.modules.indicateurs.graphe.configuration.models.GrapheConfiguration;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;

@Setter
@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tableau_bord_v2_widget")
public class TableauBordV2Widget extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dashboard_id", nullable = false)
    private TableauBordV2 dashboard;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private TableauBordV2WidgetType type;

    private String titre;

    @Column(name = "sous_titre")
    private String sousTitre;

    @Column(columnDefinition = "TEXT")
    private String description;

    @lombok.Builder.Default
    private Integer ordre = 0;

    @Column(name = "section_key")
    private String section;

    @Column(name = "x_coord")
    private Integer x;

    @Column(name = "y_coord")
    private Integer y;

    @Column(name = "width_units")
    private Integer w;

    @Column(name = "height_units")
    private Integer h;

    @Column(name = "config_json", columnDefinition = "TEXT")
    private String configJson;

    @Column(name = "style_json", columnDefinition = "TEXT")
    private String styleJson;

    @Enumerated(EnumType.STRING)
    @Column(name = "data_source_type", length = 40)
    private TableauBordV2DataSourceType dataSourceType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "indicateur_id")
    private Indicateur indicateur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "graphe_configuration_id")
    private GrapheConfiguration grapheConfiguration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chiffre_cle_id")
    private ChiffreCle chiffreCle;

    @lombok.Builder.Default
    private Boolean actif = true;

    @lombok.Builder.Default
    @OneToMany(mappedBy = "widget", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ordre ASC, id ASC")
    private List<TableauBordV2WidgetItem> items = new ArrayList<>();
}
