package ma.org.ormt.modules.espaces.dtos.request;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.Dto;
import ma.org.ormt.core.validators.groups.OnCreate;
import ma.org.ormt.core.validators.unique.Unique;

@Setter
@Getter
@Schema(name = "DemensionRequest")
@RequiredArgsConstructor
@AllArgsConstructor
@Unique.List({
        @Unique(message = "Le nom ${validatedValue.nom} existe déjà", fieldName = "nom", fieldId = "id", tableName = "espace"),
})
@JsonIgnoreProperties(value = { "indicateur.id" }, allowGetters = true)
public class EspaceRequestDto extends Dto {

    @NotBlank(message = "Ce champ est requis.")
    private String nom;

    @NotNull(message = "Ce champ est requis.", groups = OnCreate.class)
    private MultipartFile imageFile;

    @NotEmpty(message = "Ce champ est requis.")
    @Size(min = 200, max = 2000, message = "Le texte doit contenir au moins 300 caractères.")
    private String apropos;

    @NotNull(message = "Ce champ est requis.")
    private Boolean actif;

    private String description;

}