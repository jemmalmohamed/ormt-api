package ma.org.ormt.modules.partenaires.partenaire.dtos.request;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.Dto;
import ma.org.ormt.core.validators.groups.OnCreate;
import ma.org.ormt.core.validators.unique.Unique;

@Setter
@Getter
@Schema(name = "Partenaire")
@RequiredArgsConstructor
@Unique.List({
        @Unique(message = "Le nom ${validatedValue.nom} existe déjà", fieldName = "nom", fieldId = "id", tableName = "partenaire"),
})
@JsonIgnoreProperties(value = { "partenaire.id" }, allowGetters = true)
public class PartenaireRequestDto extends Dto {

    @NotBlank(message = "Ce champ est requis.")
    private String nom;

    @NotNull(message = "Ce champ est requis.", groups = OnCreate.class)
    private MultipartFile imageFile;

    @NotBlank(message = "Ce champ est requis.")
    private String siteWebUrl;

    private String description;
}