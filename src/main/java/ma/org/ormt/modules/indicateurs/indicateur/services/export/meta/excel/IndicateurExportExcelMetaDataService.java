package ma.org.ormt.modules.indicateurs.indicateur.services.export.meta.excel;

import java.util.List;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ma.org.ormt.core.utilities.ExcelUtils;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.dtos.IndicateurExportRequestDto;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.meta.facade.MetaDataCreationFacade;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.meta.models.MetaDataTable;

@Service
public class IndicateurExportExcelMetaDataService {

    @Autowired
    private MetaDataCreationFacade metaDataFacade;

    @Autowired
    private MetaDataExcelRenderer excelRenderer;

    @Autowired
    private HorizontalDimensionsExcelRenderer horizontalDimensionsRenderer;

    /**
     * Creates a basic metadata table in Excel format
     */
    public int createMetaTable(Sheet sheet, Indicateur indicateur, int startRowIdx,
            CellStyle headerStyle, CellStyle borderStyle, IndicateurExportRequestDto exportRequest) {

        // Get columns to export from request (if specified)
        List<String> columnsToExport = exportRequest != null ? exportRequest.getColumnsToExport() : null;

        MetaDataTable metaTable = metaDataFacade.createInformationMetaData(indicateur, columnsToExport);

        return excelRenderer.renderMetaDataTable(sheet, metaTable, startRowIdx, headerStyle, borderStyle);
    }

    public int createDimensionsTable(Sheet sheet, Indicateur indicateur, int startRowIdx,
            CellStyle headerStyle, CellStyle borderStyle) {

        // Create horizontal dimensions table using the specialized builder and renderer
        MetaDataTable horizontalDimensionsTable = metaDataFacade.createHorizontalDimensionsMetaData(indicateur);

        return horizontalDimensionsRenderer.renderHorizontalDimensionsTable(sheet, horizontalDimensionsTable,
                startRowIdx, headerStyle, borderStyle);
    }

    /**
     * Original domaines table format - maintained for compatibility
     */
    public int createDomainesTable(Sheet sheet, Indicateur indicateur, int rowIdx,
            CellStyle headerStyle, CellStyle borderStyle) {
        // Crée un style de header avec fond gris clair
        CellStyle customHeaderStyle = sheet.getWorkbook().createCellStyle();
        customHeaderStyle.cloneStyleFrom(headerStyle);
        Font font = sheet.getWorkbook().createFont();
        font.setBold(true);
        customHeaderStyle.setFont(font);
        customHeaderStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        customHeaderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Row domHeader = sheet.createRow(rowIdx++);
        ExcelUtils.createCell(domHeader, 0, "Domaines", customHeaderStyle);
        ExcelUtils.createCell(domHeader, 1, "Sous-domaines", customHeaderStyle);

        java.util.Set<String> domaines = new java.util.LinkedHashSet<>();
        java.util.Set<String> sousDomaines = new java.util.LinkedHashSet<>();
        if (indicateur.getSousDomaines() != null) {
            for (var sd : indicateur.getSousDomaines()) {
                if (sd.getDomaine() != null && sd.getDomaine().getNom() != null) {
                    domaines.add(sd.getDomaine().getNom());
                }
                if (sd.getNom() != null) {
                    sousDomaines.add(sd.getNom());
                }
            }
        }
        int maxDomRows = Math.max(domaines.size(), sousDomaines.size());
        var domIt = domaines.iterator();
        var sousDomIt = sousDomaines.iterator();
        for (int i = 0; i < maxDomRows; i++) {
            Row row = sheet.createRow(rowIdx++);
            ExcelUtils.createCell(row, 0, domIt.hasNext() ? domIt.next() : "", borderStyle);
            ExcelUtils.createCell(row, 1, sousDomIt.hasNext() ? sousDomIt.next() : "", borderStyle);
        }
        return rowIdx;
    }

    /**
     * Creates a configuration table with configuration metadata
     */
    public int createConfigurationTable(Sheet sheet, Indicateur indicateur, int rowIdx,
            CellStyle headerStyle, CellStyle borderStyle) {

        // Create configuration table using the facade
        MetaDataTable configTable = metaDataFacade.createConfigurationMetaData(indicateur);

        return excelRenderer.renderMetaDataTable(sheet, configTable, rowIdx, headerStyle, borderStyle);
    }

}
