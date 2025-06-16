# Metadata Export Refactoring Guide

## Overview

The metadata export functionality has been completely refactored to improve maintainability, separation of concerns, and extensibility. The new architecture provides a clean separation between:

1. **Data preparation** (metadata building)
2. **Data structuring** (metadata models)
3. **Data rendering** (Excel/other format creation)

## New Architecture

### Core Components

#### 1. Models (`models/` package)
- **`MetaDataRow`**: Represents a single label-value pair
- **`MetaDataSection`**: Represents a group of related metadata rows with a title
- **`MetaDataTable`**: Represents a complete metadata table with multiple sections

#### 2. Builders (`builders/` package)
- **`MetaDataTableBuilder`**: Main builder for creating complete metadata tables
- **`DimensionsMetaDataBuilder`**: Specialized builder for dimension-related metadata
- **`DataStatsMetaDataBuilder`**: Specialized builder for data statistics

#### 3. Renderers (`renderers/` package)
- **`MetaDataExcelRenderer`**: Renders metadata tables to Excel sheets with proper formatting

#### 4. Facade (`facade/` package)
- **`MetaDataCreationFacade`**: Provides high-level methods for creating different types of metadata tables

## Usage Examples

### Basic Usage

```java
@Autowired
private MetaDataCreationFacade metaDataFacade;

@Autowired
private MetaDataExcelRenderer excelRenderer;

// Create basic metadata
MetaDataTable basicMeta = metaDataFacade.createBasicMetaData(indicateur);

// Render to Excel sheet
int nextRowIdx = excelRenderer.renderMetaDataTable(
    sheet, basicMeta, startRowIdx, headerStyle, borderStyle);
```

### Different Metadata Types

```java
// Basic metadata - essential information only
MetaDataTable basic = metaDataFacade.createBasicMetaData(indicateur);

// Detailed metadata - comprehensive information
MetaDataTable detailed = metaDataFacade.createDetailedMetaData(indicateur);

// Compact metadata - quick overview
MetaDataTable compact = metaDataFacade.createCompactMetaData(indicateur);

// Dimensions-focused metadata
MetaDataTable dimensionsFocused = metaDataFacade.createDimensionsFocusedMetaData(indicateur);

// Data statistics focused metadata
MetaDataTable statsFocused = metaDataFacade.createDataStatsFocusedMetaData(indicateur);
```

### Using the Refactored Service

```java
@Autowired
private IndicateurExportMetaDataCreationService metaService;

// Basic metadata table
int nextRow = metaService.createMetaTable(sheet, indicateur, 0, headerStyle, borderStyle);

// Detailed metadata table
nextRow = metaService.createDetailedMetaTable(sheet, indicateur, nextRow, headerStyle, borderStyle);

// Compact metadata table
nextRow = metaService.createCompactMetaTable(sheet, indicateur, nextRow, headerStyle, borderStyle);

// Specialized tables
nextRow = metaService.createDimensionsFocusedMetaTable(sheet, indicateur, nextRow, headerStyle, borderStyle);
nextRow = metaService.createDataStatsFocusedMetaTable(sheet, indicateur, nextRow, headerStyle, borderStyle);
```

## Benefits of the New Architecture

### 1. Separation of Concerns
- **Data building** is separate from **Excel rendering**
- **Business logic** is separate from **presentation logic**
- **Specialized builders** handle specific types of metadata

### 2. Reusability
- Metadata models can be used for different output formats (Excel, PDF, JSON, etc.)
- Builders can be reused across different export scenarios
- Renderers can be swapped out for different formats

### 3. Maintainability
- Clear, focused classes with single responsibilities
- Easy to modify specific aspects without affecting others
- Better testing capabilities

### 4. Extensibility
- Easy to add new metadata sections
- Simple to create new rendering formats
- Can add new specialized builders for specific use cases

### 5. Type Safety
- Strong typing throughout the metadata creation process
- Compile-time checking of metadata structure
- Better IDE support and refactoring capabilities

## Migration Guide

### For Existing Code

The original methods are still available but marked as deprecated:

```java
// Old way (deprecated)
metaService.createDimensionsTable(sheet, indicateur, rowIdx, headerStyle, borderStyle);

// New way
metaService.createDimensionsFocusedMetaTable(sheet, indicateur, rowIdx, headerStyle, borderStyle);
```

### Recommended Migration Steps

1. **Immediate**: Use the new methods in the `IndicateurExportMetaDataCreationService`
2. **Short-term**: Replace deprecated method calls with new equivalents
3. **Long-term**: Consider using the facade and renderer directly for more complex scenarios

## Advanced Usage

### Creating Custom Metadata Tables

```java
@Component
public class CustomMetaDataBuilder {
    
    public MetaDataTable buildCustomMetaData(Indicateur indicateur) {
        MetaDataTable table = new MetaDataTable();
        
        // Create custom sections
        MetaDataSection customSection = new MetaDataSection("Custom Analysis");
        customSection.addRow("Custom Metric 1", calculateCustomMetric1(indicateur));
        customSection.addRow("Custom Metric 2", calculateCustomMetric2(indicateur));
        
        table.addSection(customSection);
        return table;
    }
}
```

### Custom Rendering

```java
public class CustomMetaDataRenderer {
    
    public void renderToJson(MetaDataTable table, JsonGenerator generator) {
        // Custom JSON rendering logic
        for (MetaDataSection section : table.getSections()) {
            // Render each section
        }
    }
}
```

## Performance Considerations

- The new architecture is more efficient for complex metadata scenarios
- Lazy evaluation where possible
- Better memory usage through focused builders
- Reduced code duplication leads to smaller bytecode

## Testing

The new architecture is much easier to test:

```java
@Test
public void testBasicMetaDataBuilding() {
    MetaDataTable table = metaDataFacade.createBasicMetaData(testIndicateur);
    assertThat(table.getSections()).hasSize(expectedSectionCount);
    assertThat(table.getTotalRowCount()).isEqualTo(expectedRowCount);
}
```

## Future Enhancements

The new architecture makes it easy to add:

1. **JSON/XML export** formats
2. **PDF metadata** sections
3. **Interactive metadata** viewers
4. **Metadata validation** and quality checks
5. **Custom metadata templates**

## Backward Compatibility

All existing functionality continues to work. The deprecated methods will be removed in a future major version, but plenty of migration time is provided.
