package ma.org.ancfcc.pva.modules.mission;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.locationtech.jts.geom.MultiPolygon;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ma.org.ancfcc.pva.core.commun.base.entity.BaseEntity;
import ma.org.ancfcc.pva.modules.capteur.Capteur;
import ma.org.ancfcc.pva.modules.objet.Objet;
import ma.org.ancfcc.pva.modules.organisme.Organisme;
import ma.org.ancfcc.pva.modules.planaction.PlanAction;

@Setter
@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = true, exclude = { "objets" })
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "mission")
public class Mission extends BaseEntity {

  private String code;

  private String nom;

  private String etat;

  private Double superficie;

  private String description;

  @Column(columnDefinition = "geometry(MultiPolygon,4326")
  private MultiPolygon delimitation;

  private LocalDate datePva;

  @ManyToOne
  @JoinColumn(name = "capteur_id")
  @JsonManagedReference
  private Capteur capteur;

  @ManyToOne
  @JoinColumn(name = "organisme_id")
  @JsonManagedReference
  private Organisme organisme;

  @ManyToOne
  @JoinColumn(name = "plan_action_id")
  @JsonManagedReference
  private PlanAction planAction;

  @Builder.Default
  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "mission_objet", joinColumns = @JoinColumn(name = "mission_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "objet_id", referencedColumnName = "id"))
  private Set<Objet> objets = new HashSet<>();

}