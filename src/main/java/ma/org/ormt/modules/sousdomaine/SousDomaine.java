package ma.org.ormt.modules.sousdomaine;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
import ma.org.ormt.modules.domaine.Domaine;

@Setter
@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "sous_domaine")
public class SousDomaine extends BaseEntity {

    private String titre;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_domaine")
    private Domaine domaine;

}
