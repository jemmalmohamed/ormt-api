package ma.org.ormt.modules.indicateurs.indicateur.dtos;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.BaseDto;
import ma.org.ormt.core.commun.base.dto.Dto;
import ma.org.ormt.modules.domaines.sousdomaine.dtos.summary.SousDomaineSummaryDto;
import ma.org.ormt.modules.indicateurs.donnee.dtos.DonneeIndicateurDto;
import ma.org.ormt.modules.indicateurs.indicateur.association.dtos.IndicateurDimensionDto;
import ma.org.ormt.modules.indicateurs.source.dtos.summary.SourceSummaryDto;
import ma.org.ormt.modules.users.AdminRoleFilter;
import ma.org.ormt.modules.users.roleacces.dtos.summary.RoleAccesSummaryDto;

@Setter
@Getter
@Schema(name = "Indicateur")
@JsonIgnoreProperties(value = { "indicateur.id" }, allowGetters = true)
@AllArgsConstructor
@NoArgsConstructor
public class IndicateurDicDto extends Dto {

    private String nom;

    private String regleCalcul;

    private Boolean actif;

    private String typeGraphe;

    private String categorie;

    private String unite;

    private String typeTb;

    private SourceSummaryDto source;

    @JsonProperty("dimensions")
    private List<IndicateurDimensionDto> indicateurDimensions;

    // private List<DonneeIndicateurDto> donnees;

    // private List<SousDomaineSummaryDto> sousDomaines;

    @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = AdminRoleFilter.class)
    private List<RoleAccesSummaryDto> roleAcces;
}