package ma.org.ormt.modules.indicateurs.indicateur.dtos.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.Dto;
import ma.org.ormt.core.validators.unique.Unique;
import ma.org.ormt.modules.indicateurs.source.dtos.SourceDto;

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

    @NotBlank(message = "Ce champ est requis.")
    private String categorie;

    @NotNull(message = "Ce champ est requis.")
    private Boolean actif;

    // @NotNull(message = "Ce champ est requis.")
    // private String typeGraphe;

    private String unite;

    @NotNull(message = "Ce champ est requis.")
    private SourceDto source;

    private String typeTb;

    @NotNull(message = "Ce champ est requis.")
    private String description;

    private String abreviation;

    private String regleCalcul;

    private String chartConfig;
}