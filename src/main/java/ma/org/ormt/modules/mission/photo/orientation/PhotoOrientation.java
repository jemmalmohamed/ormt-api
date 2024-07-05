package ma.org.ormt.modules.mission.photo.orientation;

import org.locationtech.jts.geom.Point;

import jakarta.persistence.Column;
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
import ma.org.ormt.modules.mission.photo.planification.PhotoPlanification;

@Setter
@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "photo_orientation")
public class PhotoOrientation extends BaseEntity {

    @Column(columnDefinition = "geometry(Point,4326")
    private Point centre;

    private String observation;

    private Float omega;

    private Float phi;

    private Float kappa;

    private Float altitude;

    private String geoidModel;

    private Float tempsGps;

    @OneToOne
    @JoinColumn(name = "photo_planification_id")
    private PhotoPlanification photoPlanification;

}
