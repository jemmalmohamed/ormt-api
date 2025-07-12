/**
 * TypeScript type definitions for ORMT Pivot Table Metadata
 * Generated from Java DTOs: PivotTableMetadataDto and PivotTableWithMetadataDto
 */

// ============================================================================
// PIVOT TABLE WITH METADATA
// ============================================================================

/**
 * Complete interface that contains both the pivot table data and its metadata
 * This provides all information needed for frontend chart mapping
 */
export interface PivotTableWithMetadataDto {
    /**
     * The actual pivot table data as a 2D array of strings
     * First rows are headers, remaining rows are data
     */
    pivotTableData: string[][];

    /**
     * Metadata explaining the structure and meaning of the pivot table
     */
    metadata: PivotTableMetadataDto;
}

// ============================================================================
// PIVOT TABLE METADATA
// ============================================================================

/**
 * Metadata that provides structure information about the pivot table
 * to help frontend understand how to map dimensions to chart axes
 */
export interface PivotTableMetadataDto {
    /**
     * Information about the principal dimension (forms the rows)
     */
    principalDimension: DimensionInfo;

    /**
     * Information about column dimensions (ordered from top to bottom in headers)
     * Each dimension forms one header row
     */
    columnDimensions: DimensionInfo[];

    /**
     * Number of header rows in the pivot table
     */
    headerRowCount: number;

    /**
     * Total number of data columns (after cartesian product of all column dimensions)
     */
    dataColumnCount: number;

    /**
     * Information about where data starts in the table
     */
    tableStructure: TableStructure;
}

// ============================================================================
// DIMENSION INFORMATION
// ============================================================================

/**
 * Complete information about a dimension in the pivot table
 */
export interface DimensionInfo {
    /**
     * Unique identifier for the dimension
     */
    dimensionId: number;

    /**
     * Technical name/code of the dimension
     */
    dimensionNom: string;

    /**
     * Human-readable label for the dimension
     */
    dimensionLibelle: string;

    /**
     * Type of dimension classification
     * - "temporal": Time-based dimension (years, months, etc.)
     * - "geographical": Location-based dimension (regions, cities, etc.)
     * - "categorical": Category-based dimension (sectors, types, etc.)
     */
    dimensionType: 'temporal' | 'categorical';

    /**
     * Whether this dimension represents temporal data
     */
    isTemporelle: boolean;

    /**
     * Whether this dimension is the principal dimension (forms rows)
     */
    isPrincipale: boolean;

    /**
     * Unique values for this dimension in the order they appear
     */
    values: string[];

    /**
     * Position and layout information in the table structure
     */
    position: DimensionPosition;
}

/**
 * Position and layout information for a dimension in the pivot table
 */
export interface DimensionPosition {
    /**
     * Which axis this dimension represents
     * - "row": Principal dimension (forms table rows)
     * - "column": Column dimension (forms table headers)
     */
    axis: 'row' | 'column';

    /**
     * For column dimensions: which header row (0-based from top)
     * For principal dimension: always 0
     */
    headerRowIndex: number;

    /**
     * Column range information (only for column dimensions)
     */
    columnRange?: ColumnRange;
}

/**
 * Information about how a column dimension spans across the table
 */
export interface ColumnRange {
    /**
     * Starting column index (0-based, excluding the first column which is for principal dimension)
     */
    startColumn: number;

    /**
     * Ending column index (inclusive)
     */
    endColumn: number;

    /**
     * How many columns each value of this dimension spans
     * Useful for understanding header repetition patterns
     */
    valueSpan: number;
}

// ============================================================================
// TABLE STRUCTURE
// ============================================================================

/**
 * Information about the overall structure and layout of the pivot table
 */
export interface TableStructure {
    /**
     * Row index where actual data starts (after all header rows)
     */
    dataStartRow: number;

    /**
     * Column index where data starts (after principal dimension column)
     */
    dataStartColumn: number;

    /**
     * Total number of rows in the table (headers + data rows)
     */
    totalRows: number;

    /**
     * Total number of columns in the table (principal column + data columns)
     */
    totalColumns: number;
}

// ============================================================================
// UTILITY TYPES
// ============================================================================

/**
 * Chart mapping configuration for different visualization needs
 */
export interface ChartMappingConfig {
    /**
     * Dimension to use for X-axis
     */
    xAxis: string;

    /**
     * Dimension to use for series grouping
     */
    series: string;

    /**
     * Y-axis is always the indicator values
     */
    yAxis: 'valeur';

    /**
     * Dimensions to use as filters (dimension name -> selected value)
     */
    filters: Record<string, string>;
}

/**
 * Helper type for chart type compatibility
 */
export type ChartType = 'line' | 'bar' | 'pie' | 'doughnut' | 'area' | 'scatter' | 'radar' | 'polar';

/**
 * Available chart mapping strategies
 */
export interface ChartMappingStrategy {
    /**
     * Chart type this strategy is designed for
     */
    chartType: ChartType;

    /**
     * Recommended mapping configuration
     */
    mapping: ChartMappingConfig;

    /**
     * Whether this strategy requires temporal data
     */
    requiresTemporal: boolean;

    /**
     * Maximum number of series this chart type can handle
     */
    maxSeries?: number;
}
