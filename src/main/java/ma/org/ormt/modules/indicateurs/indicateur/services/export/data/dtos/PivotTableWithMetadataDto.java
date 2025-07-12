package ma.org.ormt.modules.indicateurs.indicateur.services.export.data.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Complete DTO that contains both the pivot table data and its metadata
 * This provides all information needed for frontend chart mapping
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PivotTableWithMetadataDto {

    /**
     * The actual pivot table data (as returned by
     * IndicateurPivotDataTable.buildPivotTableData)
     */
    private List<List<String>> data;

    /**
     * Metadata explaining the structure and meaning of the pivot table
     */
    private PivotTableMetadataDto metadata;

}
