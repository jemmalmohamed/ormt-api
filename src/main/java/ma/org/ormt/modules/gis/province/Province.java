package ma.org.ormt.modules.gis.province;

import org.locationtech.jts.geom.MultiPolygon;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ma.org.ormt.core.commun.base.entity.BaseEntity;
import ma.org.ormt.modules.gis.region.Region;

@Setter
@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "province")
public class Province extends BaseEntity {

    private String nom;

    private Long superficie;

    private String description;

    private String typeCollectivite;

    private MultiPolygon delimitation;

    @OneToOne
    @JoinColumn(name = "region_id")
    private Region region;

}
