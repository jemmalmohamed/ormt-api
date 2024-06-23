package ma.org.ancfcc.pva.modules.mission;

import java.time.LocalDate;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ma.org.ancfcc.pva.core.commun.base.entity.BaseEntity;
import ma.org.ancfcc.pva.modules.organisme.Organisme;
import ma.org.ancfcc.pva.modules.planaction.PlanAction;

@Setter
@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "mission")
public class Mission extends BaseEntity {

  private String code;

  private String nom;

  private String etat;

  private Long superficie;

  private String description;

  @Column(columnDefinition = "geometry")
  private Geometry delimitation;

  private LocalDate datePva;

  @ManyToOne
  @JoinColumn(name = "organisme_id")
  @JsonManagedReference
  private Organisme organisme;

  @ManyToOne
  @JoinColumn(name = "plan_action_id")
  @JsonManagedReference
  private PlanAction planAction;

}