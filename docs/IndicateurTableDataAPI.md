# Indicateur Table Data API

## Overview
The `IndicateurDetailDto` now supports additional table data formats for easier frontend consumption. This new functionality provides both pivot table and flat table formats without changing the existing API structure.

## Usage

### Basic Request (Existing Functionality)
```http
GET /api/v1/admin/indicateurs/{id}
```
Returns the standard `IndicateurDetailDto` with `indicateurDimensions` and `donnees` fields.

### With Table Data
```http
GET /api/v1/admin/indicateurs/{id}?tableFormat=pivot
GET /api/v1/admin/indicateurs/{id}?tableFormat=flat  
GET /api/v1/admin/indicateurs/{id}?tableFormat=both
```

## Table Formats

### Pivot Table Format (`tableFormat=pivot`)
- Uses the `principale` dimension as rows
- Uses other dimensions as columns (with `temporelle` dimension prioritized first)
- Perfect for analysis, charts, and executive dashboards
- Returns data in `pivotTableData` field

### Flat Table Format (`tableFormat=flat`)
- Each row represents one data point
- All dimensions are flattened as columns
- Perfect for CRUD operations, filtering, and data entry
- Returns data in `flatTableData` field

### Both Formats (`tableFormat=both`)
- Returns both pivot and flat table data
- Useful when frontend needs flexibility

## Response Structure

```json
{
  "data": {
    "id": 123,
    "nom": "Indicateur Example",
    "unite": "%",
    "typeGraphe": "line",
    "indicateurDimensions": [...],
    "donnees": [...],
    
    // Only present when tableFormat is specified
    "pivotTableData": [
      ["Genre", "2022", "2023"],
      ["Féminin", "22.8", "18.5"],
      ["Masculin", "25.1", "21.2"]
    ],
    
    "flatTableData": [
      ["Genre", "Milieu", "Année", "Valeur"],
      ["Féminin", "Urbain", "2023", "18.5"],
      ["Féminin", "Rural", "2022", "22.8"],
      ["Masculin", "Urbain", "2023", "21.2"]
    ]
  }
}
```

## Benefits

1. **Backward Compatibility**: Existing API calls work unchanged
2. **Performance**: Table data only generated when requested
3. **Flexibility**: Choose the format that best suits your use case
4. **Chart Ready**: Pivot format works directly with most chart libraries
5. **CRUD Ready**: Flat format perfect for data tables and editing

## Frontend Usage Examples

### For Charts (Pivot Format)
```javascript
const response = await fetch('/api/v1/admin/indicateurs/123?tableFormat=pivot');
const data = await response.json();
const chartData = data.data.pivotTableData;
// Use directly with Chart.js, D3, etc.
```

### For Data Tables (Flat Format)
```javascript
const response = await fetch('/api/v1/admin/indicateurs/123?tableFormat=flat');
const data = await response.json();
const tableData = data.data.flatTableData;
// Use with ag-Grid, Ant Design Table, etc.
```

### For Complex UIs (Both Formats)
```javascript
const response = await fetch('/api/v1/admin/indicateurs/123?tableFormat=both');
const data = await response.json();
const { pivotTableData, flatTableData } = data.data;
// Use pivot for charts, flat for editing
```
