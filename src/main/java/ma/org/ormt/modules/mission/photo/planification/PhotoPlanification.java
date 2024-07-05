package ma.org.ormt.modules.mission.photo.planification;

import org.locationtech.jts.geom.Point;

import jakarta.persistence.CascadeType;
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
import ma.org.ormt.modules.mission.bande.Bande;
import ma.org.ormt.modules.mission.photo.execution.PhotoExecution;
import ma.org.ormt.modules.mission.photo.orientation.PhotoOrientation;

@Setter
@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "photo_planification")
public class PhotoPlanification extends BaseEntity {

    private String nom;

    private String label;

    private String observation;

    @Column(columnDefinition = "geometry(Point,4326")
    private Point centre;

    @OneToOne
    @JoinColumn(name = "bande_id")
    private Bande bande;

    @OneToOne(mappedBy = "photoPlanification", cascade = CascadeType.ALL)
    private PhotoExecution photoExecution;

    @OneToOne(mappedBy = "photoPlanification", cascade = CascadeType.ALL)
    private PhotoOrientation photoOrientation;

}
