package ma.org.ormt.modules.dashboard.tableaubord.v2.models;

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
@Table(name = "tableau_bord_v2_widget_item")
public class TableauBordV2WidgetItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "widget_id", nullable = false)
    private TableauBordV2Widget widget;

    private String libelle;

    private String valeur;

    private String unite;

    @Column(columnDefinition = "TEXT")
    private String description;

    @lombok.Builder.Default
    private Integer ordre = 0;

    @Column(name = "config_json", columnDefinition = "TEXT")
    private String configJson;

    @Column(name = "style_json", columnDefinition = "TEXT")
    private String styleJson;

    @lombok.Builder.Default
    private Boolean actif = true;
}
