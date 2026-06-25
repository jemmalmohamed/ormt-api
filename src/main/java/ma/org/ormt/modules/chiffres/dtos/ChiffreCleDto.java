package ma.org.ormt.modules.chiffres.dtos;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.Dto;
import ma.org.ormt.modules.chiffres.models.enums.KpiEvolutionMode;
import ma.org.ormt.modules.chiffres.models.enums.KpiFormatType;
import ma.org.ormt.modules.chiffres.models.enums.KpiModeSource;
import ma.org.ormt.modules.indicateurs.donnee.dtos.DonneeIndicateurDto;
import ma.org.ormt.modules.roleacces.dtos.summary.RoleAccesSummaryDto;
import ma.org.ormt.security.users.RoleAccesContentFilter;

@Setter
@Getter
@Schema(name = "ChiffreCle")
@JsonIgnoreProperties(value = { "chiffrecle.id" }, allowGetters = true)
@AllArgsConstructor
@NoArgsConstructor
public class ChiffreCleDto extends Dto {

    private String libelle;

    private String valeur;

    private String unite;

    private String description;

    private Boolean actif;

    private Boolean afficherDate;

    private Boolean afficherDescription;

    private String accessType;

    private KpiModeSource modeSource;

    private KpiFormatType formatType;

    private String prefixLabel;

    private String suffixLabel;

    private KpiEvolutionMode evolutionMode;

    private String metadataJson;

    private String styleJson;

    private DonneeIndicateurDto donneeIndicateur;

    private Dto indicateur;

    private String indicateurNom;

    private DonneeReferenceDto donneeReference;

    @JsonInclude(content = JsonInclude.Include.CUSTOM, contentFilter = RoleAccesContentFilter.class)
    private List<RoleAccesSummaryDto> roleAcces;

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DonneeReferenceDto {
        private String indicateurNom;
        private String valeur;
        private List<DimensionValueDto> dimensions;
    }

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DimensionValueDto {
        private String dimensionNom;
        private String valeur;
    }

}
