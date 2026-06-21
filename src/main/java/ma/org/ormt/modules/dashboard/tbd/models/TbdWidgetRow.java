package ma.org.ormt.modules.dashboard.tbd.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Table(name = "tbd_widget_row")
public class TbdWidgetRow extends BaseEntity {

    @Column(nullable = false)
    private Long sectionId;

    @Builder.Default
    private Integer ordre = 0;

    @Builder.Default
    private Integer sizePercent = 50;

    @Builder.Default
    @Column(nullable = false)
    private Integer heightPx = 200;
}
