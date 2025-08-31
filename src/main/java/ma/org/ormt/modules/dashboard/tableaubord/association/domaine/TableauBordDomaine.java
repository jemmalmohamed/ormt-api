package ma.org.ormt.modules.dashboard.tableaubord.association.domaine;

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
import ma.org.ormt.modules.dashboard.tableaubord.models.TableauBord;

@Setter
@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tableau_bord_tb_domaine")
public class TableauBordDomaine extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "id_tableau_bord")
    private TableauBord tableauBord;

    @ManyToOne
    @JoinColumn(name = "id_tb_domaine")
    private TBDomaine tbDomaine;

    private Integer ordre;

}
