package ma.org.ormt.modules.dashboard.tbd.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "tbd_assignation")
public class TbdAssignation extends BaseEntity {

    @Column(nullable = false)
    private Long dashboardId;

    @Column(nullable = false, length = 20)
    private String cibleType;

    @Column(nullable = false)
    private Long cibleId;
}
