package ma.org.ancfcc.pva.modules.planaction;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ma.org.ancfcc.pva.core.commun.base.entity.BaseEntity;
import ma.org.ancfcc.pva.modules.mission.Mission;

@Setter
@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "plan_action")
public class PlanAction extends BaseEntity {

    private String nom;

    private String description;

    @Column(name = "debut_date")
    private LocalDate debutDate;

    @Column(name = "fin_date")
    private LocalDate finDate;

    @OneToMany(mappedBy = "planAction")
    @JsonBackReference
    private List<Mission> missions;

}
