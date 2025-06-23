package ma.org.ormt.modules.publications.publication.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.BaseDto;

import java.time.LocalDate;

@Setter
@Getter
@Schema(name = "Publication")
@JsonIgnoreProperties(value = { "publication.id" }, allowGetters = true)
@AllArgsConstructor
@NoArgsConstructor
public class PublicationDto extends BaseDto {

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
