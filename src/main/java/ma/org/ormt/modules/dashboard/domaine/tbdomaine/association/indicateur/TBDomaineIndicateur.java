package ma.org.ormt.modules.dashboard.domaine.tbdomaine.association.indicateur;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ma.org.ormt.core.commun.base.entity.BaseEntity;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.models.TBDomaine;

import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;

@Setter
@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tb_domaine_indicateur")
public class TBDomaineIndicateur extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "id_tb_domaine")
    private TBDomaine tbDomaine;

    @ManyToOne
    @JoinColumn(name = "id_indicateur")
    private Indicateur indicateur;

    private String categorie;

    private Integer ordre;

}
