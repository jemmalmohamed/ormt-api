package ma.org.ormt.modules.dashboard.tbd.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ma.org.ormt.core.commun.base.entity.BaseEntity;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;

@Setter
@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbd_widget")
public class TbdWidget extends BaseEntity {

    @Column(nullable = false)
    private Long rowId;

    @Builder.Default
    @Column(nullable = false, length = 20)
    private String type = "CHART";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "indicateur_id")
    private Indicateur indicateur;

    private Long kpiId;

    @Column(columnDefinition = "TEXT")
    private String contentJson;

    private String titre;

    @Builder.Default
    private Integer ordre = 0;

    @Builder.Default
    private Integer sizePercent = 50;

    @Builder.Default
    @Column(nullable = false)
    private Boolean actif = true;
}
