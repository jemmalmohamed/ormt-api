package ma.org.ormt.modules.chiffres.dtos.request;

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
import ma.org.ormt.modules.chiffres.models.enums.KpiEvolutionMode;
import ma.org.ormt.modules.chiffres.models.enums.KpiFormatType;
import ma.org.ormt.modules.chiffres.models.enums.KpiModeSource;

@Setter
@Getter
@Schema(name = "DemensionRequest")
@RequiredArgsConstructor
@AllArgsConstructor
@Unique.List({
        @Unique(message = "Le libellé ${validatedValue.libelle} existe déjà", fieldName = "libelle", fieldId = "id", tableName = "chiffre_cle"),
})
@JsonIgnoreProperties(value = { "indicateur.id" }, allowGetters = true)
public class ChiffreCleRequestDto extends Dto {

    @NotBlank(message = "Ce champ est requis.")
    private String libelle;

    private String valeur;

    private String unite;

    @NotNull(message = "Ce champ est requis.")
    private Boolean actif;

    private Boolean afficherDate;

    private Boolean afficherDescription;

    private String description;

    private String accessType;

    private KpiModeSource modeSource;

    private KpiFormatType formatType;

    private String prefixLabel;

    private String suffixLabel;

    private KpiEvolutionMode evolutionMode;

    private String metadataJson;

    private String styleJson;

    private Dto indicateur;

    private Dto donneeIndicateur;
}
