package ma.org.ormt.modules.mission.bande;

import java.util.ArrayList;
import java.util.List;

import org.locationtech.jts.geom.LineString;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ma.org.ormt.core.commun.base.entity.BaseEntity;
import ma.org.ormt.modules.mission.models.Mission;
import ma.org.ormt.modules.mission.photo.planification.PhotoPlanification;

@Setter
@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bande")
public class Bande extends BaseEntity {

  private String nom;

  private String label;

  private String observation;

  @Column(columnDefinition = "geometry(LineString,4326")
  private LineString axePlanification;

  @ManyToOne
  @JoinColumn(name = "mission_id")
  private Mission mission;

  @Builder.Default()
  @OneToMany(mappedBy = "bande", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<PhotoPlanification> photoPlanifications = new ArrayList<>();

}