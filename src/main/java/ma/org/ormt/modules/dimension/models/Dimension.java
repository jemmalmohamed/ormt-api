package ma.org.ormt.modules.dimension.models;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ma.org.ormt.core.commun.base.entity.BaseEntity;
import ma.org.ormt.modules.indicateur.models.Indicateur;

@Setter
@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "dimension")
public class Dimension extends BaseEntity {

    @Column(unique = true)
    private String nom;

    private String type;

    private String description;

    private String libelle;

    @Builder.Default
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "indicateur_dimension", joinColumns = @JoinColumn(name = "id_dimension", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "id_indicateur", referencedColumnName = "id"))
    private List<Indicateur> indicateurs = new ArrayList<>();
}