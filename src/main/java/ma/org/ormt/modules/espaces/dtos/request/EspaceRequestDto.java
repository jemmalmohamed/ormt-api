package ma.org.ormt.modules.espaces.dtos.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.Dto;
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

    @NotBlank(message = "Ce champ est requis.")
    private String image;

    @NotBlank(message = "Ce champ est requis.")
    private String apropos;

    @NotBlank(message = "Ce champ est requis.")
    private String role;

    @NotBlank(message = "Ce champ est requis.")
    private String statut;

    private String description;

}