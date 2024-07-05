package ma.org.ormt.modules.capteur;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

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
import ma.org.ormt.modules.mission.models.Mission;

@Setter
@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "capteur")
public class Capteur extends BaseEntity {

    private String categorie;

    private String nom;

    private String code;

    private String serial;

    private String mode;

    private String format;

    private String constructeur;

    private String description;

    @OneToMany(mappedBy = "capteur")
    @JsonBackReference
    private List<Mission> missions;

}
