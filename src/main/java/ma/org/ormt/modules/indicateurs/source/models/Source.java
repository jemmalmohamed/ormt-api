package ma.org.ormt.modules.indicateurs.source.models;

import java.util.List;

import jakarta.persistence.Column;
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
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;

@Setter
@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "source")
public class Source extends BaseEntity {

    @Column(unique = true)
    private String nom;

    private String description;

    private String url;

    private String abreviation;

    @OneToMany(mappedBy = "source")
    private List<Indicateur> indicateurs;

}