package ma.org.ormt.modules.dashboard.tableaubord.models;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ma.org.ormt.core.commun.base.entity.BaseEntity;
import ma.org.ormt.modules.dashboard.tableaubord.association.domaine.TableauBordDomaine;

@Setter
@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tableau_bord")
public class TableauBord extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String nom;

    @lombok.Builder.Default
    private boolean actif = true;

    private String description;

    @OneToMany(mappedBy = "tableauBord", fetch = FetchType.LAZY)
    private List<TableauBordDomaine> tableauBordDomaines;

}