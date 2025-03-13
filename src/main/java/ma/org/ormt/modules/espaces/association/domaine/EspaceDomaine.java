package ma.org.ormt.modules.espaces.association.domaine;

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
import ma.org.ormt.modules.domaines.domaine.models.Domaine;
import ma.org.ormt.modules.espaces.models.Espace;

@Setter
@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "espace_domaine")
public class EspaceDomaine extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "id_espace")
    private Espace espace;

    @ManyToOne
    @JoinColumn(name = "id_domaine")
    private Domaine domaine;

}
