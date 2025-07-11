package ma.org.ormt.modules.indicateurs.indicateur.services.export.meta.excel;

import java.util.List;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Service;

import ma.org.ormt.core.utilities.ExcelUtils;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.meta.models.MetaDataRow;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.meta.models.MetaDataSection;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.meta.models.MetaDataTable;

/**
 * Specialized Excel renderer for horizontal dimensions layout
 * This preserves the original horizontal columns approach
 */
@Service
public class HorizontalDimensionsExcelRenderer {

    /**
     * Renders horizontal dimensions table to Excel with the original layout
     */
    public int renderHorizontalDimensionsTable(Sheet sheet, MetaDataTable metaTable, int startRowIdx,
            CellStyle headerStyle, CellStyle borderStyle) {

        if (metaTable == null || metaTable.getSections().isEmpty()) {
            return startRowIdx;
        }

        // Create custom header style like the original
        CellStyle customHeaderStyle = sheet.getWorkbook().createCellStyle();
        customHeaderStyle.cloneStyleFrom(headerStyle);
        Font font = sheet.getWorkbook().createFont();
        font.setBold(true);
        customHeaderStyle.setFont(font);
        customHeaderStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        customHeaderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Add global header
        Row globalHeader = sheet.createRow(startRowIdx++);
        ExcelUtils.createCell(globalHeader, 0, "Dimensions", customHeaderStyle);

        int currentRowIdx = startRowIdx;
        int maxRows = 0;

        // Process the first (and main) section which contains horizontal data
        MetaDataSection dimensionsSection = metaTable.getSections().get(0);

        // Find dimension columns from the header row
        List<String> dimensionNames = extractDimensionNames(dimensionsSection);
        if (dimensionNames.isEmpty()) {
            // No dimensions case
            Row row = sheet.createRow(currentRowIdx);
            ExcelUtils.createCell(row, 0, "Aucune dimension définie", borderStyle);
            return currentRowIdx + 1;
        }

        // Render each dimension column
        int colIdx = 0;
        for (int dimIndex = 0; dimIndex < dimensionNames.size(); dimIndex++) {
            int rowIdx = startRowIdx;

            // Create vertical column for this dimension
            rowIdx = renderDimensionColumn(sheet, dimensionsSection, dimIndex, colIdx,
                    rowIdx, headerStyle, borderStyle);

            int totalRows = rowIdx - startRowIdx;
            maxRows = Math.max(maxRows, totalRows);
            colIdx += 2; // Each dimension takes 2 columns (label + value)
        }

        return startRowIdx + maxRows;
    }

    /**
     * Extracts dimension names from the header row
     */
    private List<String> extractDimensionNames(MetaDataSection section) {
        // Look for the header row
        for (MetaDataRow row : section.getRows()) {
            if ("Header".equals(row.getLabel())) {
                String[] parts = row.getValue().split("\\|");
                List<String> names = new java.util.ArrayList<>();
                for (int i = 0; i < parts.length; i += 2) { // Skip empty columns
                    if (!parts[i].trim().isEmpty()) {
                        names.add(parts[i].trim());
                    }
                }
                return names;
            }
        }
        return new java.util.ArrayList<>();
    }

    /**
     * Renders a single dimension column
     */
    private int renderDimensionColumn(Sheet sheet, MetaDataSection section, int dimensionIndex,
            int colIdx, int startRowIdx, CellStyle headerStyle, CellStyle borderStyle) {
        int rowIdx = startRowIdx;

        // Render basic properties
        rowIdx = renderProperty(sheet, section, "Nom_Row", dimensionIndex, colIdx, rowIdx, headerStyle, borderStyle);
        rowIdx = renderProperty(sheet, section, "Libelle_Row", dimensionIndex, colIdx, rowIdx, headerStyle,
                borderStyle);
        rowIdx = renderProperty(sheet, section, "Principale_Row", dimensionIndex, colIdx, rowIdx, headerStyle,
                borderStyle);
        rowIdx = renderProperty(sheet, section, "Temporelle_Row", dimensionIndex, colIdx, rowIdx, headerStyle,
                borderStyle);
        rowIdx = renderProperty(sheet, section, "Description_Row", dimensionIndex, colIdx, rowIdx, headerStyle,
                borderStyle);

        // Render values
        rowIdx = renderValues(sheet, section, dimensionIndex, colIdx, rowIdx, headerStyle, borderStyle);

        return rowIdx;
    }

    /**
     * Renders a property row for a specific dimension
     */
    private int renderProperty(Sheet sheet, MetaDataSection section, String rowKey, int dimensionIndex,
            int colIdx, int rowIdx, CellStyle headerStyle, CellStyle borderStyle) {

        for (MetaDataRow row : section.getRows()) {
            if (rowKey.equals(row.getLabel())) {
                String[] parts = row.getValue().split("\\|");
                int targetIndex = dimensionIndex * 2; // Each dimension has 2 columns

                if (targetIndex < parts.length && targetIndex + 1 < parts.length) {
                    Row excelRow = sheet.getRow(rowIdx) != null ? sheet.getRow(rowIdx) : sheet.createRow(rowIdx);
                    ExcelUtils.createCell(excelRow, colIdx, parts[targetIndex], headerStyle);
                    ExcelUtils.createCell(excelRow, colIdx + 1, parts[targetIndex + 1], borderStyle);
                    return rowIdx + 1;
                }
                break;
            }
        }
        return rowIdx;
    }

    /**
     * Renders values for a specific dimension
     */
    private int renderValues(Sheet sheet, MetaDataSection section, int dimensionIndex, int colIdx,
            int rowIdx, CellStyle headerStyle, CellStyle borderStyle) {

        // First, add "Valeurs" header if this is the first dimension
        boolean headerAdded = false;
        for (MetaDataRow row : section.getRows()) {
            if ("Valeurs_Header".equals(row.getLabel()) && !headerAdded) {
                Row valeursHeaderRow = sheet.getRow(rowIdx) != null ? sheet.getRow(rowIdx) : sheet.createRow(rowIdx);
                ExcelUtils.createCell(valeursHeaderRow, colIdx, "Valeurs", headerStyle);
                // Add merged region for this dimension
                sheet.addMergedRegion(
                        new org.apache.poi.ss.util.CellRangeAddress(rowIdx, rowIdx, colIdx, colIdx + 1));
                rowIdx++;
                headerAdded = true;
                break;
            }
        }

        // Then add value rows
        for (MetaDataRow row : section.getRows()) {
            if (row.getLabel().startsWith("Value_")) {
                String[] parts = row.getValue().split("\\|");
                int targetIndex = dimensionIndex * 2 + 1; // Values go in the second column of each dimension pair

                if (targetIndex < parts.length && !parts[targetIndex].trim().isEmpty()) {
                    Row valueRow = sheet.getRow(rowIdx) != null ? sheet.getRow(rowIdx) : sheet.createRow(rowIdx);
                    ExcelUtils.createCell(valueRow, colIdx + 1, parts[targetIndex], borderStyle);
                    rowIdx++;
                }
            }
        }

        return rowIdx;
    }
}
