package ma.org.ormt.modules.gis.spatial_ref_sys;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "spatial_ref_sys")
public class SpatialRefSys {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer srid;

  private Integer authSrid;

  private String authName;

  private String srtext;

  private String proj4text;

}