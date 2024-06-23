package ma.org.ancfcc.pva.modules.capteur;

import jakarta.persistence.Entity;

import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ma.org.ancfcc.pva.core.commun.base.entity.BaseEntity;

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

    private String serial;

    private String mode;

    private String format;

    private String constructeur;

    private String description;

}
