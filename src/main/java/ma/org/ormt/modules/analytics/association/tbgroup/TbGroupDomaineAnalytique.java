package ma.org.ormt.modules.analytics.association.tbgroup;

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
import ma.org.ormt.modules.analytics.domain.models.DomaineAnalytique;
import ma.org.ormt.modules.dashboard.tbgroup.models.TbGroup;

@Setter
@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tb_group_domaine_analytique")
public class TbGroupDomaineAnalytique extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "id_tb_group")
    private TbGroup tbGroup;

    @ManyToOne
    @JoinColumn(name = "id_domaine_analytique")
    private DomaineAnalytique domaineAnalytique;

    private Integer ordre;
}
