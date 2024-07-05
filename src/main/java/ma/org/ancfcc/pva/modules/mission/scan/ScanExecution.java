package ma.org.ancfcc.pva.modules.mission.scan;

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
import ma.org.ancfcc.pva.core.commun.base.entity.BaseEntity;
import ma.org.ancfcc.pva.modules.mission.bande.Bande;

@Setter
@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "scan_execution")
public class ScanExecution extends BaseEntity {

    private String nom;

    private String label;

    private LocalDate datePva;

    private String observation;

    @Column(columnDefinition = "geometry(Polygon,4326")
    private Polygon emprise;

    @OneToOne
    @JoinColumn(name = "bande_id")
    private Bande bande;

}
