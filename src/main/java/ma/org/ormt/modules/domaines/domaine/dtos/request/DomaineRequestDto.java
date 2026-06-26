package ma.org.ormt.modules.domaines.domaine.dtos.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.Dto;
import ma.org.ormt.core.validators.unique.Unique;

@Setter
@Getter
@Schema(name = "Domaine")
@RequiredArgsConstructor
@Unique.List({
        @Unique(message = "Le nom ${validatedValue.nom} existe déjà", fieldName = "nom", fieldId = "id", tableName = "domaine"),
})
@JsonIgnoreProperties(value = { "domaine.id" }, allowGetters = true)
public class DomaineRequestDto extends Dto {

    @NotBlank(message = "Ce champ est requis.")
    private String nom;

    @NotNull(message = "Ce champ est requis.")
    private Boolean actif;

    @NotBlank(message = "Ce champ est requis.")
    private String description;
}
