package ma.org.ormt.modules.indicateurs.indicateur.dtos;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.BaseDto;
import ma.org.ormt.modules.domaines.sousdomaine.dtos.summary.SousDomaineSummaryDto;
import ma.org.ormt.modules.indicateurs.source.dtos.summary.SourceSummaryDto;
import ma.org.ormt.modules.roleacces.dtos.summary.RoleAccesSummaryDto;
import ma.org.ormt.security.users.AdminRoleFilter;

@Setter
@Getter
@Schema(name = "Indicateur")
@JsonIgnoreProperties(value = { "indicateur.id" }, allowGetters = true)
@AllArgsConstructor
@NoArgsConstructor
public class IndicateurDto extends BaseDto {

    private String nom;

    private String regleCalcul;

    private Boolean actif;

    private String typeGraphe;

    private String categorie;

    private String description;

    private String unite;

    private String typeTb;

    private SourceSummaryDto source;

    private boolean hasDonnees;

    private List<SousDomaineSummaryDto> sousDomaines;

    @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = AdminRoleFilter.class)
    private List<RoleAccesSummaryDto> roleAcces;
}