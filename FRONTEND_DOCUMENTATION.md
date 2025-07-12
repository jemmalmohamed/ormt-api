# Pivot Table Metadata API - Frontend Documentation

## 📋 Overview

The Pivot Table Metadata API provides structured information about pivot tables to enable intelligent chart mapping and visualization in the frontend. This API eliminates the guesswork of understanding which rows and columns correspond to which dimensions.

## 🔗 API Endpoint

```http
GET /api/v1/admin/indicateurs/{id}?tableFormat=pivot
```

**Response includes:**
- `pivotTableWithMetadata`: Enhanced pivot data with structural metadata

## 📊 Data Structure

### API Response Structure
```typescript
interface IndicateurDetailDto {
  id: number;
  nom: string;
  description: string;
  // ... other indicator fields
  
  pivotTableWithMetadata: PivotTableWithMetadataDto;
}
```

### Complete Example Response
```json
{
  "id": 123,
  "nom": "Emploi par secteur",
  "description": "Indicateur d'emploi réparti par secteur d'activité",
  "pivotTableWithMetadata": {
    "pivotTableData": [
      ["secteur", "2019", "2019", "2020", "2020"],
      ["", "urbain", "rural", "urbain", "rural"],
      ["Agriculture", "12.5", "15.3", "13.1", "16.2"],
      ["Industrie", "8.7", "9.2", "8.9", "9.8"],
      ["Services", "22.1", "18.4", "23.7", "19.1"]
    ],
    "metadata": {
      "principalDimension": {
        "dimensionId": 1,
        "dimensionNom": "secteur",
        "dimensionLibelle": "Secteur d'activité",
        "dimensionType": "categorical",
        "isTemporelle": false,
        "isPrincipale": true,
        "values": ["Agriculture", "Industrie", "Services"],
        "position": {
          "axis": "row",
          "headerRowIndex": 0
        }
      },
      "columnDimensions": [
        {
          "dimensionId": 2,
          "dimensionNom": "annee",
          "dimensionLibelle": "Année",
          "dimensionType": "temporal",
          "isTemporelle": true,
          "isPrincipale": false,
          "values": ["2019", "2020"],
          "position": {
            "axis": "column",
            "headerRowIndex": 0,
            "columnRange": {
              "startColumn": 1,
              "endColumn": 4,
              "valueSpan": 2
            }
          }
        },
        {
          "dimensionId": 3,
          "dimensionNom": "zone",
          "dimensionLibelle": "Zone géographique",
          "dimensionType": "geographical",
          "isTemporelle": false,
          "isPrincipale": false,
          "values": ["urbain", "rural"],
          "position": {
            "axis": "column",
            "headerRowIndex": 1,
            "columnRange": {
              "startColumn": 1,
              "endColumn": 4,
              "valueSpan": 1
            }
          }
        }
      ],
      "headerRowCount": 2,
      "dataColumnCount": 4,
      "tableStructure": {
        "dataStartRow": 2,
        "dataStartColumn": 1,
        "totalRows": 5,
        "totalColumns": 5
      }
    }
  }
}
```

## 🎯 Understanding the Structure

### Principal Dimension (Rows)
- **Always forms the table rows**
- **Located in the first column** (index 0)
- **Identified by**: `isPrincipale: true`
- **Use for**: Series grouping, category axis

### Column Dimensions (Headers)
- **Form the table headers** (multiple rows if multiple dimensions)
- **Ordered from top to bottom**
- **Each dimension gets one header row**
- **Use for**: X-axis, filters, secondary grouping

### Header Structure
The pivot table headers are organized as follows:
```
Row 0: [principal_label, year1, year1, year2, year2]    // First column dimension
Row 1: ["", zone1, zone2, zone1, zone2]                // Second column dimension
Row 2: [value1, data, data, data, data]                // Data starts here
Row 3: [value2, data, data, data, data]
```

## 🛠️ Frontend Implementation

### 1. Basic Data Extraction

```typescript
import { PivotTableWithMetadataDto, DimensionInfo } from './types';

class PivotTableParser {
  constructor(private pivotData: PivotTableWithMetadataDto) {}

  /**
   * Get all available dimensions for mapping
   */
  getAvailableDimensions(): DimensionInfo[] {
    return [
      this.pivotData.metadata.principalDimension,
      ...this.pivotData.metadata.columnDimensions
    ];
  }

  /**
   * Find a dimension by name
   */
  findDimension(dimensionNom: string): DimensionInfo | undefined {
    return this.getAvailableDimensions()
      .find(dim => dim.dimensionNom === dimensionNom);
  }

  /**
   * Get temporal dimensions (ideal for X-axis)
   */
  getTemporalDimensions(): DimensionInfo[] {
    return this.getAvailableDimensions()
      .filter(dim => dim.isTemporelle);
  }

  /**
   * Get categorical dimensions (good for series/grouping)
   */
  getCategoricalDimensions(): DimensionInfo[] {
    return this.getAvailableDimensions()
      .filter(dim => dim.dimensionType === 'categorical');
  }
}
```

### 2. Chart Mapping Component

```typescript
@Component({
  selector: 'app-chart-dimension-mapper',
  template: `
    <div class="dimension-mapper">
      <h4>Configure Chart Mapping</h4>
      
      <!-- Chart Type Selection -->
      <div class="mapping-group">
        <label>Chart Type:</label>
        <select [(ngModel)]="selectedChartType" (change)="onChartTypeChange()">
          <option *ngFor="let type of availableChartTypes" [value]="type">
            {{ getChartTypeLabel(type) }}
          </option>
        </select>
      </div>

      <!-- X-Axis Mapping -->
      <div class="mapping-group">
        <label>X-Axis:</label>
        <select [(ngModel)]="chartMapping.xAxis" (change)="onMappingChange()">
          <option *ngFor="let dim of getXAxisOptions()" [value]="dim.dimensionNom">
            {{ dim.dimensionLibelle }}
            <span *ngIf="dim.isTemporelle" class="badge temporal">Temporal</span>
          </option>
        </select>
      </div>

      <!-- Series Mapping -->
      <div class="mapping-group">
        <label>Series Grouping:</label>
        <select [(ngModel)]="chartMapping.series" (change)="onMappingChange()">
          <option *ngFor="let dim of getSeriesOptions()" [value]="dim.dimensionNom">
            {{ dim.dimensionLibelle }}
            <span *ngIf="dim.isPrincipale" class="badge principal">Principal</span>
          </option>
        </select>
      </div>

      <!-- Filters -->
      <div class="mapping-group">
        <label>Filters:</label>
        <div *ngFor="let dim of getFilterOptions()" class="filter-row">
          <label class="checkbox-label">
            <input type="checkbox" 
                   [checked]="isFilterActive(dim.dimensionNom)"
                   (change)="toggleFilter(dim.dimensionNom)">
            {{ dim.dimensionLibelle }}
          </label>
          <select *ngIf="isFilterActive(dim.dimensionNom)"
                  [(ngModel)]="chartMapping.filters[dim.dimensionNom]"
                  (change)="onMappingChange()">
            <option *ngFor="let value of dim.values" [value]="value">
              {{ value }}
            </option>
          </select>
        </div>
      </div>

      <!-- Preview -->
      <div class="preview-section">
        <h5>Mapping Preview</h5>
        <pre>{{ getMappingPreview() | json }}</pre>
      </div>
    </div>
  `
})
export class ChartDimensionMapperComponent {
  @Input() pivotData!: PivotTableWithMetadataDto;
  @Output() mappingChanged = new EventEmitter<ChartMappingConfig>();

  selectedChartType: ChartType = 'bar';
  chartMapping: ChartMappingConfig = {
    xAxis: '',
    series: '',
    yAxis: 'valeur',
    filters: {}
  };

  availableChartTypes: ChartType[] = ['line', 'bar', 'pie', 'area'];

  private parser!: PivotTableParser;

  ngOnInit() {
    this.parser = new PivotTableParser(this.pivotData);
    this.initializeDefaultMapping();
  }

  private initializeDefaultMapping() {
    // Auto-suggest temporal dimension for X-axis
    const temporalDim = this.parser.getTemporalDimensions()[0];
    if (temporalDim) {
      this.chartMapping.xAxis = temporalDim.dimensionNom;
    }

    // Auto-suggest principal dimension for series
    const principalDim = this.pivotData.metadata.principalDimension;
    if (principalDim) {
      this.chartMapping.series = principalDim.dimensionNom;
    }

    this.onMappingChange();
  }

  getXAxisOptions(): DimensionInfo[] {
    // Temporal dimensions are best for X-axis
    const temporal = this.parser.getTemporalDimensions();
    if (temporal.length > 0) return temporal;
    
    // Fallback to all dimensions
    return this.parser.getAvailableDimensions();
  }

  getSeriesOptions(): DimensionInfo[] {
    // All dimensions except the one selected for X-axis
    return this.parser.getAvailableDimensions()
      .filter(dim => dim.dimensionNom !== this.chartMapping.xAxis);
  }

  getFilterOptions(): DimensionInfo[] {
    // All dimensions except those used for X-axis and series
    return this.parser.getAvailableDimensions()
      .filter(dim => 
        dim.dimensionNom !== this.chartMapping.xAxis &&
        dim.dimensionNom !== this.chartMapping.series
      );
  }

  isFilterActive(dimensionNom: string): boolean {
    return dimensionNom in this.chartMapping.filters;
  }

  toggleFilter(dimensionNom: string) {
    if (this.isFilterActive(dimensionNom)) {
      delete this.chartMapping.filters[dimensionNom];
    } else {
      const dimension = this.parser.findDimension(dimensionNom);
      if (dimension && dimension.values.length > 0) {
        this.chartMapping.filters[dimensionNom] = dimension.values[0];
      }
    }
    this.onMappingChange();
  }

  onChartTypeChange() {
    // Adjust mapping based on chart type requirements
    if (this.selectedChartType === 'pie' || this.selectedChartType === 'doughnut') {
      // Pie charts need categorical data
      const categoricalDim = this.parser.getCategoricalDimensions()[0];
      if (categoricalDim) {
        this.chartMapping.xAxis = categoricalDim.dimensionNom;
        this.chartMapping.series = '';
      }
    }
    this.onMappingChange();
  }

  onMappingChange() {
    this.mappingChanged.emit({ ...this.chartMapping });
  }

  getMappingPreview() {
    return {
      chartType: this.selectedChartType,
      mapping: this.chartMapping,
      dimensions: {
        xAxis: this.parser.findDimension(this.chartMapping.xAxis)?.dimensionLibelle,
        series: this.parser.findDimension(this.chartMapping.series)?.dimensionLibelle,
        filters: Object.entries(this.chartMapping.filters).map(([key, value]) => ({
          dimension: this.parser.findDimension(key)?.dimensionLibelle,
          value
        }))
      }
    };
  }

  getChartTypeLabel(type: ChartType): string {
    const labels = {
      line: 'Line Chart',
      bar: 'Bar Chart', 
      pie: 'Pie Chart',
      doughnut: 'Doughnut Chart',
      area: 'Area Chart',
      scatter: 'Scatter Plot',
      radar: 'Radar Chart',
      polar: 'Polar Chart'
    };
    return labels[type] || type;
  }
}
```

### 3. Data Transformation Service

```typescript
@Injectable()
export class PivotToChartDataService {

  /**
   * Convert pivot table data to Chart.js format based on mapping configuration
   */
  transformToChartData(
    pivotData: PivotTableWithMetadataDto, 
    mapping: ChartMappingConfig
  ): ChartConfiguration {
    
    const metadata = pivotData.metadata;
    const tableData = pivotData.pivotTableData;
    
    // Find mapped dimensions
    const xAxisDim = this.findDimension(metadata, mapping.xAxis);
    const seriesDim = this.findDimension(metadata, mapping.series);
    
    // Extract labels (X-axis values)
    const labels = this.extractLabels(tableData, metadata, xAxisDim);
    
    // Extract datasets (series)
    const datasets = this.extractDatasets(
      tableData, 
      metadata, 
      xAxisDim, 
      seriesDim, 
      mapping.filters
    );
    
    return {
      type: 'bar', // or dynamic based on chart type
      data: {
        labels,
        datasets
      },
      options: {
        responsive: true,
        plugins: {
          title: {
            display: true,
            text: this.buildChartTitle(metadata, mapping)
          }
        }
      }
    };
  }

  private findDimension(metadata: PivotTableMetadataDto, dimensionNom: string): DimensionInfo | undefined {
    if (metadata.principalDimension.dimensionNom === dimensionNom) {
      return metadata.principalDimension;
    }
    return metadata.columnDimensions.find(dim => dim.dimensionNom === dimensionNom);
  }

  private extractLabels(
    tableData: string[][], 
    metadata: PivotTableMetadataDto,
    xAxisDim: DimensionInfo | undefined
  ): string[] {
    
    if (!xAxisDim) return [];
    
    if (xAxisDim.position.axis === 'row') {
      // X-axis is the principal dimension (rows)
      return tableData
        .slice(metadata.headerRowCount)
        .map(row => row[0]);
    } else {
      // X-axis is in column headers
      const headerRow = tableData[xAxisDim.position.headerRowIndex];
      return this.getUniqueValues(headerRow.slice(1)); // Skip first column
    }
  }

  private extractDatasets(
    tableData: string[][],
    metadata: PivotTableMetadataDto,
    xAxisDim: DimensionInfo | undefined,
    seriesDim: DimensionInfo | undefined,
    filters: Record<string, string>
  ): ChartDataset[] {
    
    if (!xAxisDim || !seriesDim) return [];
    
    // Apply filters first
    const filteredData = this.applyFilters(tableData, metadata, filters);
    
    if (seriesDim.position.axis === 'row') {
      // Series are rows (principal dimension)
      return this.createDatasetsFromRows(filteredData, metadata);
    } else {
      // Series are columns
      return this.createDatasetsFromColumns(filteredData, metadata, seriesDim);
    }
  }

  private applyFilters(
    tableData: string[][],
    metadata: PivotTableMetadataDto,
    filters: Record<string, string>
  ): string[][] {
    // Implementation depends on filter logic
    // This is a simplified version
    return tableData;
  }

  private getUniqueValues(array: string[]): string[] {
    return Array.from(new Set(array)).filter(v => v !== '');
  }

  private buildChartTitle(metadata: PivotTableMetadataDto, mapping: ChartMappingConfig): string {
    const xAxisDim = this.findDimension(metadata, mapping.xAxis);
    const seriesDim = this.findDimension(metadata, mapping.series);
    
    return `${seriesDim?.dimensionLibelle || 'Values'} by ${xAxisDim?.dimensionLibelle || 'Category'}`;
  }

  // Additional helper methods...
}
```

## 🔍 Common Use Cases

### 1. Time Series Chart (Line/Area)
```typescript
// Ideal mapping for temporal data
const timeSeriesMapping: ChartMappingConfig = {
  xAxis: 'annee',        // Temporal dimension
  series: 'secteur',     // Principal dimension
  yAxis: 'valeur',
  filters: {
    zone: 'urbain'       // Filter to specific zone
  }
};
```

### 2. Categorical Comparison (Bar Chart)
```typescript
// Good for comparing categories
const categoricalMapping: ChartMappingConfig = {
  xAxis: 'secteur',      // Principal/categorical dimension
  series: 'zone',        // Secondary dimension  
  yAxis: 'valeur',
  filters: {
    annee: '2020'        // Filter to specific year
  }
};
```

### 3. Distribution Chart (Pie/Doughnut)
```typescript
// Single dimension breakdown
const distributionMapping: ChartMappingConfig = {
  xAxis: 'secteur',      // Categories
  series: '',            // No series grouping
  yAxis: 'valeur',
  filters: {
    annee: '2020',
    zone: 'urbain'
  }
};
```

## ⚡ Performance Tips

1. **Cache parsed metadata** - The structure doesn't change between mapping updates
2. **Debounce mapping changes** - Avoid excessive re-renders during user interaction
3. **Lazy load charts** - Only render charts when mapping is complete
4. **Validate mappings** - Check dimension compatibility before transformation

## 🐛 Error Handling

```typescript
class ChartMappingValidator {
  static validate(
    pivotData: PivotTableWithMetadataDto, 
    mapping: ChartMappingConfig
  ): ValidationResult {
    
    const errors: string[] = [];
    const warnings: string[] = [];
    
    // Check if dimensions exist
    const parser = new PivotTableParser(pivotData);
    
    if (!parser.findDimension(mapping.xAxis)) {
      errors.push(`X-axis dimension '${mapping.xAxis}' not found`);
    }
    
    if (mapping.series && !parser.findDimension(mapping.series)) {
      errors.push(`Series dimension '${mapping.series}' not found`);
    }
    
    // Check for logical conflicts
    if (mapping.xAxis === mapping.series) {
      errors.push('X-axis and series cannot use the same dimension');
    }
    
    // Performance warnings
    const xAxisDim = parser.findDimension(mapping.xAxis);
    if (xAxisDim && xAxisDim.values.length > 50) {
      warnings.push('Large number of X-axis values may impact performance');
    }
    
    return {
      isValid: errors.length === 0,
      errors,
      warnings
    };
  }
}

interface ValidationResult {
  isValid: boolean;
  errors: string[];
  warnings: string[];
}
```

## 📚 Additional Resources

- **TypeScript Definitions**: `TYPESCRIPT_DEFINITIONS.ts`
- **Java DTOs**: `PivotTableMetadataDto.java`, `PivotTableWithMetadataDto.java`
- **API Documentation**: Standard REST API docs
- **Chart.js Integration**: Official Chart.js documentation

This metadata system provides complete transparency into the pivot table structure, enabling sophisticated chart mapping capabilities while maintaining type safety and performance.
