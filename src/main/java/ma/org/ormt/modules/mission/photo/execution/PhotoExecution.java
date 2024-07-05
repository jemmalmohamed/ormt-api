package ma.org.ormt.modules.mission.photo.execution;

import java.time.LocalDate;

import org.locationtech.jts.geom.Polygon;

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
@Table(name = "photo_execution")
public class PhotoExecution extends BaseEntity {

    @Column(columnDefinition = "geometry(Polygon,4326")
    private Polygon emprise;

    private String observation;

    private LocalDate datePva;

    private String bobine;

    @OneToOne
    @JoinColumn(name = "photo_planification_id")
    private PhotoPlanification photoPlanification;

}
