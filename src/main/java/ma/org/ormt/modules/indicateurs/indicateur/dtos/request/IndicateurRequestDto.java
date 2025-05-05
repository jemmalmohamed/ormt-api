package ma.org.ormt.modules.indicateurs.indicateur.dtos.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.Dto;
import ma.org.ormt.core.validators.unique.Unique;
import ma.org.ormt.modules.indicateurs.source.models.Source;

@Setter
@Getter
@Schema(name = "IndicateurRequest")
@RequiredArgsConstructor
@AllArgsConstructor
@Unique.List({
        @Unique(message = "Le nom ${validatedValue.nom} existe déjà", fieldName = "nom", fieldId = "id", tableName = "indicateur"),
})
@JsonIgnoreProperties(value = { "indicateur.id" }, allowGetters = true)
public class IndicateurRequestDto extends Dto {

    @NotBlank(message = "Ce champ est requis.")
    private String nom;

    private String description;

    private String abreviation;

    @NotNull(message = "Ce champ est requis.")
    private Boolean actif;

    @NotBlank(message = "Ce champ est requis.")
    private Source source;

    private String regleCalcul;

    @NotBlank(message = "Ce champ est requis.")
    private String unite;

    @NotBlank(message = "Ce champ est requis.")
    private String typeTb;

}