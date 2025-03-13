package ma.org.ormt.modules.indicateurs.source.dtos.request;

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
        @Unique(message = "Le nom ${validatedValue.nom} existe déjà", fieldName = "nom", fieldId = "id", tableName = "source"),
})
@JsonIgnoreProperties(value = { "indicateur.id" }, allowGetters = true)
public class SourceRequestDto extends Dto {

    @NotBlank(message = "Ce champ est requis.")
    private String nom;
    @NotBlank(message = "Ce champ est requis.")

    @NotBlank(message = "Ce champ est requis.")
    private String libelle;

    @NotBlank(message = "Ce champ est requis.")
    private String type;

    private String description;

}