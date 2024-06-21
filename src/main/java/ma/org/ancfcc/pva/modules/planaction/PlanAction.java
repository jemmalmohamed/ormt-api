package ma.org.ancfcc.pva.modules.planaction;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ma.org.ancfcc.pva.core.commun.base.entity.BaseEntity;

@Setter
@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "plan_action")
public class PlanAction extends BaseEntity<UUID> {

    private String designation;

    private String description;

    @Column(name = "debut_date")
    private LocalDateTime debutDate;

    @Column(name = "fin_date")
    private LocalDateTime finDate;

}
