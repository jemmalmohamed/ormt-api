package ma.org.ancfcc.pva.modules.objet;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ma.org.ancfcc.pva.core.commun.base.entity.BaseEntity;
import ma.org.ancfcc.pva.modules.mission.models.Mission;

@Setter
@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = false, exclude = { "missions" })
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "objet")
public class Objet extends BaseEntity {

    private String nom;

    private String description;

    @Builder.Default
    @ManyToMany(mappedBy = "objets")
    private Set<Mission> missions = new HashSet<>();

}
