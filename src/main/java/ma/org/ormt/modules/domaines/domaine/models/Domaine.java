package ma.org.ormt.modules.domaines.domaine.models;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
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
import ma.org.ormt.modules.domaines.sousdomaine.models.SousDomaine;
import ma.org.ormt.modules.espaces.association.domaine.EspaceDomaine;

@Setter
@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "domaine")
public class Domaine extends BaseEntity {

    @Column(unique = true)
    private String nom;

    private String imageUrl;

    private String apropos;

    private String description;

    private Boolean actif;

    @OneToMany(mappedBy = "domaine", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SousDomaine> sousDomaines;

    @Builder.Default
    @OneToMany(mappedBy = "domaine", fetch = FetchType.EAGER)
    private List<EspaceDomaine> espaceDomaines = new ArrayList<>();

}
