package ma.org.ormt.modules.indicateurs.indicateur.dtos.detail;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.modules.indicateurs.donnee.dtos.DonneeIndicateurDto;
import ma.org.ormt.modules.indicateurs.graphe.configuration.dtos.GrapheConfigurationDto;
import ma.org.ormt.modules.indicateurs.indicateur.association.dimension.dtos.IndicateurDimensionDto;
import ma.org.ormt.modules.indicateurs.indicateur.dtos.IndicateurDto;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.data.dtos.PivotTableWithMetadataDto;

@Setter
@Getter
@Schema(name = "IndicateurDetailDto")
@RequiredArgsConstructor
@JsonIgnoreProperties(value = { "indicateur.id" }, allowGetters = true)
public class IndicateurDetailDto extends IndicateurDto {

    private List<DonneeIndicateurDto> donnees;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "Flat table format data for CRUD operations")
    private List<List<String>> flatTableData;

    // CRUD-specific table data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "CRUD table format with IDs for edit/delete   operations")
    private List<List<String>> crudTableData;

    // @JsonInclude(JsonInclude.Include.NON_NULL)
    // @Schema(description = "Template data for create operations (missing
    // combinations)")
    // private List<List<String>> createTemplateData;

    // Enhanced pivot table with metadata for chart mapping
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "Pivot table data with metadata for chart mapping and dimension understanding")
    private PivotTableWithMetadataDto pivotTableData;

    private List<GrapheConfigurationDto> grapheConfigurations;

}