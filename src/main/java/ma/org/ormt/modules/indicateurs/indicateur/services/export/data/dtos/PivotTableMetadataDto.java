package ma.org.ormt.modules.indicateurs.indicateur.services.export.data.dtos;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Metadata DTO that provides structure information about the pivot table
 * to help frontend understand how to map dimensions to chart axes
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PivotTableMetadataDto {

    /**
     * Information about the principal dimension (rows)
     */
    private DimensionInfo principalDimension;

    /**
     * Information about column dimensions (ordered from top to bottom in headers)
     */
    private List<DimensionInfo> columnDimensions;

    /**
     * Number of header rows in the pivot table
     */
    private int headerRowCount;

    /**
     * Total number of data columns (after choroplethsian product)
     */
    private int dataColumnCount;

    /**
     * Information about where data starts in the table
     */
    private TableStructure tableStructure;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DimensionInfo {
        /**
         * Dimension ID
         */
        private Long dimensionId;

        /**
         * Dimension name/code
         */
        private String dimensionNom;

        /**
         * Display label for the dimension
         */
        private String dimensionLibelle;

        /**
         * Type of dimension (temporal, geographical, categorical)
         */
        private String dimensionType;

        /**
         * Whether this dimension is temporal
         */
        private boolean isTemporelle;

        /**
         * Whether this dimension is principal
         */
        private boolean isPrincipale;

        /**
         * Unique values for this dimension (in order)
         */
        private List<String> values;

        /**
         * Position in the table structure
         */
        private DimensionPosition position;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DimensionPosition {
        /**
         * For principal dimension: always "row"
         * For column dimensions: "column"
         */
        private String axis; // "row" or "column"

        /**
         * For column dimensions: which header row (0-based)
         * For principal dimension: always 0
         */
        private int headerRowIndex;

        /**
         * Column range this dimension spans (for column dimensions)
         */
        private ColumnRange columnRange;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ColumnRange {
        /**
         * Starting column index (0-based, excluding the first column which is for
         * principal dimension)
         */
        private int startColumn;

        /**
         * Ending column index (inclusive)
         */
        private int endColumn;

        /**
         * How many columns each value of this dimension spans
         */
        private int valueSpan;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TableStructure {
        /**
         * Row index where data starts (after headers)
         */
        private int dataStartRow;

        /**
         * Column index where data starts (after principal dimension column)
         */
        private int dataStartColumn;

        /**
         * Total number of rows in the table (headers + data)
         */
        private int totalRows;

        /**
         * Total number of columns in the table
         */
        private int totalColumns;
    }
}
