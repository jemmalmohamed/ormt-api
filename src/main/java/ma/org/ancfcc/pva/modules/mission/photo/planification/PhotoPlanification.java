package ma.org.ancfcc.pva.modules.mission.photo.planification;

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
import ma.org.ancfcc.pva.core.commun.base.entity.BaseEntity;
import ma.org.ancfcc.pva.modules.mission.bande.Bande;

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

    private String commentaire;

    @Column(columnDefinition = "geometry(Point,4326")
    private Point center;

    @OneToOne
    @JoinColumn(name = "bande_id")
    private Bande bande;

}
