package ma.org.ormt.modules.indicateurs.indicateur.dtos.series;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.Dto;
import ma.org.ormt.modules.domaines.sousdomaine.dtos.summary.SousDomaineSummaryDto;
import ma.org.ormt.modules.indicateurs.source.dtos.summary.SourceSummaryDto;
import ma.org.ormt.modules.users.AdminRoleFilter;
import ma.org.ormt.modules.users.roleacces.dtos.summary.RoleAccesSummaryDto;

@Setter
@Getter
@Schema(name = "Indicateur")
@JsonIgnoreProperties(value = { "indicateur.id" }, allowGetters = true)
@AllArgsConstructor
@NoArgsConstructor
public class IndicateurChartDto extends Dto {

    private String nom;

    private String description;

    private String regleCalcul;

    private Boolean actif;

    private String categorie;

    private String unite;

    private String typeTb;

    private SourceSummaryDto source;

    private List<SousDomaineSummaryDto> sousDomaines;

    @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = AdminRoleFilter.class)
    private List<RoleAccesSummaryDto> roleAcces;

    // Chart configuration
    private String temporalDimension; // Name of time dimension (e.g., "Année", "Mois")
    private String principalDimension; // Name of dimension used for series (e.g., "Region")
    private List<String> availableDimensions; // All dimensions names available for this indicator

    // Chart data
    private List<ChartSeriesDto> series; // The default series data (typically principal dimension)

    private List<EnhancedChartSeriesDto> enhancedSeries;
    // All series for every dimension
    private Map<String, List<ChartSeriesDto>> allDimensionSeries;

    // Combined series for multi-dimension visualization
    private List<ChartSeriesDto> combinedSeries;

    // Filter options
    private List<DimensionValueDto> filterOptions; // Available dimension values for filtering
}