package ma.org.ormt.modules.partenaires.partenaire.dtos.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.Dto;
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

    @NotBlank(message = "Ce champ est requis.")
    private String role;

    @NotBlank(message = "Ce champ est requis.")
    private String statut;

    @NotBlank(message = "Ce champ est requis.")
    private String description;

    @NotBlank(message = "Ce champ est requis.")
    private String apropos;

}