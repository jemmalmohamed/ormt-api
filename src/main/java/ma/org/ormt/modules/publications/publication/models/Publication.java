package ma.org.ormt.modules.publications.publication.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ma.org.ormt.core.commun.base.entity.BaseEntity;

import java.time.LocalDate;

@Setter
@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "publication")
public class Publication extends BaseEntity {

    private String titre;

    private String description;

    private String auteur;

    private LocalDate datePublication;

    private String fichierUrl;

    private String nomFichier;

    private Long tailleFichier;

    private String categorie;

    private String tags;

    private Integer nombreTelechargements;

}
