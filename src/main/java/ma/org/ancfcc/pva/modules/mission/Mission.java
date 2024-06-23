package ma.org.ancfcc.pva.modules.mission;

import java.time.LocalDate;

import org.locationtech.jts.geom.MultiPolygon;

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
@Table(name = "mission")
public class Mission extends BaseEntity {

  private String code;

  private String nom;

  private String etat;

  private Long superficie;

  private String description;

  private MultiPolygon delimitation;

  private LocalDate datePva;

}