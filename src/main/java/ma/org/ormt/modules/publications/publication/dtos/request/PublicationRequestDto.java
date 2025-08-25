package ma.org.ormt.modules.publications.publication.dtos.request;

import java.time.LocalDate;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.Dto;
import ma.org.ormt.core.validators.file.FileSize;
import ma.org.ormt.core.validators.groups.OnCreate;
import ma.org.ormt.core.validators.unique.Unique;

@Setter
@Getter
@Schema(name = "Publication")
@RequiredArgsConstructor
@Unique.List({
        @Unique(message = "Le titre ${validatedValue.titre} existe déjà", fieldName = "titre", fieldId = "id", tableName = "publication"),
})
@JsonIgnoreProperties(value = { "publication.id" }, allowGetters = true)
public class PublicationRequestDto extends Dto {

    @NotBlank(message = "Ce champ est requis.")
    private String titre;

    @NotBlank(message = "Ce champ est requis.")
    private String description;

    @NotBlank(message = "Ce champ est requis.")
    private String auteur;

    @NotNull(message = "Ce champ est requis.")
    private LocalDate datePublication;

    @NotNull(message = "Ce champ est requis.", groups = OnCreate.class)
    @FileSize(max = 20 * 1024 * 1024, message = "La taille du fichier ne doit pas dépasser 20 Mo")
    private MultipartFile fichier;

    private String titreFichier;

    private Long tailleFichier;

    private String categorie;

    private String tags;

    private Integer nombreTelechargements;

    @NotNull(message = "Ce champ est requis.")
    private Boolean actif;

}
