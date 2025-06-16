package ma.org.ormt.modules.indicateurs.indicateur.services.export.meta.excel;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Component;

import ma.org.ormt.core.utilities.ExcelUtils;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.meta.models.MetaDataRow;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.meta.models.MetaDataSection;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.meta.models.MetaDataTable;

/**
 * Renders metadata tables to Excel sheets
 */
@Component
public class MetaDataExcelRenderer {

    /**
     * Renders a complete metadata table to an Excel sheet
     */
    public int renderMetaDataTable(Sheet sheet, MetaDataTable table, int startRowIdx,
            CellStyle headerStyle, CellStyle borderStyle) {
        if (table == null || table.isEmpty()) {
            return startRowIdx;
        }

        int currentRowIdx = startRowIdx;

        for (MetaDataSection section : table.getSections()) {
            currentRowIdx = renderSection(sheet, section, currentRowIdx, headerStyle, borderStyle);
            // Add a blank row between sections
            currentRowIdx++;
        }

        return currentRowIdx;
    }

    /**
     * Renders a single section to the Excel sheet
     */
    private int renderSection(Sheet sheet, MetaDataSection section, int startRowIdx,
            CellStyle headerStyle, CellStyle borderStyle) {
        if (section == null || section.isEmpty()) {
            return startRowIdx;
        }

        int currentRowIdx = startRowIdx;

        // Add section title if present
        if (section.getTitle() != null && !section.getTitle().trim().isEmpty()) {
            Row titleRow = sheet.createRow(currentRowIdx++);
            CellStyle sectionHeaderStyle = createSectionHeaderStyle(sheet, headerStyle);
            ExcelUtils.createCell(titleRow, 0, section.getTitle(), sectionHeaderStyle);
            // Merge the title across two columns
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(
                    currentRowIdx - 1, currentRowIdx - 1, 0, 1));
        }

        // Add all rows in the section
        for (MetaDataRow row : section.getRows()) {
            currentRowIdx = renderRow(sheet, row, currentRowIdx, headerStyle, borderStyle);
        }

        return currentRowIdx;
    }

    /**
     * Renders a single metadata row
     */
    private int renderRow(Sheet sheet, MetaDataRow row, int rowIdx,
            CellStyle headerStyle, CellStyle borderStyle) {
        if (row == null) {
            return rowIdx;
        }

        Row excelRow = sheet.createRow(rowIdx);
        ExcelUtils.createCell(excelRow, 0, row.getLabel(), headerStyle);
        ExcelUtils.createCell(excelRow, 1, row.getValue(), borderStyle);

        return rowIdx + 1;
    }

    /**
     * Creates a style for section headers
     */
    private CellStyle createSectionHeaderStyle(Sheet sheet, CellStyle baseHeaderStyle) {
        CellStyle sectionHeaderStyle = sheet.getWorkbook().createCellStyle();
        sectionHeaderStyle.cloneStyleFrom(baseHeaderStyle);

        // Make it more prominent
        org.apache.poi.ss.usermodel.Font font = sheet.getWorkbook().createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        sectionHeaderStyle.setFont(font);

        // Different background color for section headers
        sectionHeaderStyle.setFillForegroundColor(
                org.apache.poi.ss.usermodel.IndexedColors.GREY_40_PERCENT.getIndex());
        sectionHeaderStyle.setFillPattern(
                org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);

        return sectionHeaderStyle;
    }
}
