package ma.org.ormt.modules.domaines.domaine.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ma.org.ormt.core.commun.base.entity.BaseEntity;
import ma.org.ormt.modules.domaines.sousdomaine.models.SousDomaine;

import java.util.List;

@Setter
@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "domaine")
public class Domaine extends BaseEntity {

    private String nom;

    private String description;

    @OneToMany(mappedBy = "domaine", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SousDomaine> sousDomaines;

}
