package ma.org.ormt.modules.indicateurs.indicateur.dtos.sousdomaine;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.Dto;
import ma.org.ormt.modules.indicateurs.graphe.configuration.dtos.GrapheConfigurationDto;
import ma.org.ormt.modules.indicateurs.indicateur.association.dimension.dtos.IndicateurDimensionDto;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.data.dtos.PivotTableWithMetadataDto;
import ma.org.ormt.modules.indicateurs.source.dtos.summary.SourceSummaryDto;

@Setter
@Getter
@Schema(name = "IndicateurSousDomaineDetailDto")
@RequiredArgsConstructor
@JsonIgnoreProperties(value = { "indicateur.id" }, allowGetters = true)
public class IndicateurSousDomaineDetailDto extends Dto {

    private String nom;

    private String titre;

    private Boolean actif;

    private String categorie;

    private String description;

    private String unite;

    private Boolean regional;

    private String territoire;

    private SourceSummaryDto source;

    private List<GrapheConfigurationDto> grapheConfigurations;

    private List<IndicateurDimensionDto> indicateurDimensions;

    // Enhanced pivot table with metadata for chart mapping
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "Pivot table data with metadata for chart mapping and dimension understanding")
    private PivotTableWithMetadataDto pivotTableData;

}