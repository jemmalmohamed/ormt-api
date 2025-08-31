package ma.org.ormt.modules.dashboard.domaine.tbdomaine.models;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Table(name = "tb_domaine")
public class TBDomaine extends BaseEntity {

    @Column(unique = true)
    private String nom;

    private String description;

    private Boolean actif;

    // @OneToMany(mappedBy = "tbDomaine", cascade = CascadeType.ALL, orphanRemoval =
    // true)
    // private List<SousDomaine> sousDomaines;

    @Builder.Default
    @OneToMany(mappedBy = "tbDomaine", fetch = FetchType.LAZY)
    private List<TableauBordDomaine> tableauBordDomaines = new ArrayList<>();

}
