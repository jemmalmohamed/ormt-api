package ma.org.ormt.modules.espaces.models;

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
import ma.org.ormt.modules.espaces.association.domaine.EspaceDomaine;

@Setter
@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "espace")
public class Espace extends BaseEntity {

    @Column(unique = true)
    private String nom;

    private String imageUrl;

    private String apropos;

    private String description;

    private boolean actif;

    @OneToMany(mappedBy = "espace", fetch = FetchType.LAZY)
    private List<EspaceDomaine> espaceDomaines;

}