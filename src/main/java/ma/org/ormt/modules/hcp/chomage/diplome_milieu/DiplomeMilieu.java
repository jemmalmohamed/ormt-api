package ma.org.ormt.modules.hcp.chomage.diplome_milieu;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ma.org.ormt.core.commun.base.entity.BaseEntity;

@Setter
@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "taux_chomage_diplome_milieu")
public class DiplomeMilieu extends BaseEntity {

    private String annee;

    private String diplome;

    private String milieu;

    private float taux;

}
