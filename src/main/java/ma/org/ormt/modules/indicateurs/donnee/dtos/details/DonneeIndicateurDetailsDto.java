package ma.org.ormt.modules.indicateurs.donnee.dtos.details;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.Dto;
import ma.org.ormt.modules.indicateurs.donnee.dtos.DonneeIndicateurDto;

@Setter
@Getter
@Schema(name = "DonneeIndicateurDetailsDto")
@RequiredArgsConstructor
@JsonIgnoreProperties(value = { "donneIndicateur.id" }, allowGetters = true)
public class DonneeIndicateurDetailsDto extends DonneeIndicateurDto {

    Dto indicateur;

    // // Add table data - only included when requested
    // @JsonInclude(JsonInclude.Include.NON_NULL)
    // @Schema(description = "Pivot table format data for display purposes")
    // private List<List<String>> pivotTableData;

    // @JsonInclude(JsonInclude.Include.NON_NULL)
    // @Schema(description = "Flat table format data for CRUD operations")
    // private List<List<String>> flatTableData;

    // // CRUD-specific table data
    // @JsonInclude(JsonInclude.Include.NON_NULL)
    // @Schema(description = "CRUD table format with IDs for edit/delete
    // operations")
    // private List<List<String>> crudTableData;

    // @JsonInclude(JsonInclude.Include.NON_NULL)
    // @Schema(description = "Template data for create operations (missing
    // combinations)")
    // private List<List<String>> createTemplateData;
}