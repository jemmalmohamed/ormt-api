package ma.org.ormt.modules.indicateurs.indicateur.services.export.single.formats.xls;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import ma.org.ormt.core.utilities.ExcelUtils;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.data.builders.IndicateurFlatDataTable;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.data.builders.IndicateurPivotDataTable;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.dtos.IndicateurExportRequestDto;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.meta.excel.IndicateurExportExcelMetaDataService;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.meta.facade.MetaDataCreationFacade;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.meta.excel.MetaDataExcelRenderer;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.meta.models.MetaDataTable;

@Service
public class IndicateurSingleExportExcelServiceImpl implements IndicateurSingleExportExcelService {

    @Autowired
    private IndicateurExportExcelMetaDataService indicateurExportExcelMetaDataService;

    @Autowired
    private MetaDataCreationFacade metaDataFacade;

    @Autowired
    private MetaDataExcelRenderer metaDataRenderer;

    /**
     * Enhanced export method with advanced options
     */
    @Override
    public ResponseEntity<byte[]> exportIndicateurWithOptions(Indicateur indicateur,
            IndicateurExportRequestDto exportRequest)
            throws IOException {
        if (indicateur == null) {
            return ResponseEntity.badRequest().body(new byte[0]);
        }

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            var borderStyle = ExcelUtils.createBorderedStyle(workbook);
            var headerStyle = ExcelUtils.createHeaderStyle(workbook);

            // Get sections to export
            List<String> sectionsToExport = exportRequest.getSectionsToExport();

            if (sectionsToExport == null || sectionsToExport.isEmpty()) {
                // Default to all sections if none specified
                sectionsToExport = List.of(
                        IndicateurExportRequestDto.ExportSection.META.getKey(),
                        IndicateurExportRequestDto.ExportSection.DOMAINES.getKey(),
                        IndicateurExportRequestDto.ExportSection.CONFIGURATION.getKey(),
                        IndicateurExportRequestDto.ExportSection.DIMENSIONS.getKey(),
                        IndicateurExportRequestDto.ExportSection.PIVOT_DATA.getKey(),
                        IndicateurExportRequestDto.ExportSection.FLAT_DATA.getKey());
            }

            // Create sheets based on requested sections
            for (String section : sectionsToExport) {
                switch (section) {
                    case "META":
                        createMetadataSheet(workbook, indicateur, headerStyle, borderStyle, exportRequest);
                        break;
                    case "DOMAINES":
                        createDomainesSheet(workbook, indicateur, headerStyle, borderStyle);
                        break;
                    case "DIMENSIONS":
                        createDimensionsSheet(workbook, indicateur, headerStyle, borderStyle);
                        break;
                    case "PIVOT_DATA":
                        if (shouldIncludePivotData(exportRequest.getDataTableType())) {
                            createPivotDataSheet(workbook, indicateur);
                        }
                        break;
                    case "FLAT_DATA":
                        if (shouldIncludeFlatData(exportRequest.getDataTableType())) {
                            createFlatDataSheet(workbook, indicateur);
                        }
                        break;
                }
            }

            workbook.write(out);
            String fileName = exportRequest.getFileName() != null ? exportRequest.getFileName()
                    : "indicateur-" + indicateur.getId();
            return ExcelUtils.exportExcelFormat(out, fileName);
        }
    }

    /**
     * Creates enhanced metadata sheet with full details
     */
    private void createMetadataSheet(Workbook workbook, Indicateur indicateur,
            org.apache.poi.ss.usermodel.CellStyle headerStyle,
            org.apache.poi.ss.usermodel.CellStyle borderStyle,
            IndicateurExportRequestDto exportRequest) {
        Sheet metaSheet = workbook.createSheet("Informations générales");

        indicateurExportExcelMetaDataService.createMetaTable(
                metaSheet, indicateur, 0,
                headerStyle, borderStyle, exportRequest);

        ExcelUtils.autoSizeColumns(metaSheet);
    }

    /**
     * Creates domaines analysis sheet using facade pattern
     */
    private void createDomainesSheet(Workbook workbook, Indicateur indicateur,
            org.apache.poi.ss.usermodel.CellStyle headerStyle,
            org.apache.poi.ss.usermodel.CellStyle borderStyle) {
        Sheet domainesSheet = workbook.createSheet("Domaines et sous-domaines");

        // Step 1: Create structured metadata using facade
        MetaDataTable domainesMetaData = metaDataFacade.createDomainesMetaData(indicateur);

        // Step 2: Render to Excel using renderer
        metaDataRenderer.renderMetaDataTable(domainesSheet, domainesMetaData, 0, headerStyle, borderStyle);

        ExcelUtils.autoSizeColumns(domainesSheet);
    }

    /**
     * Creates a dedicated dimensions analysis sheet using the original table format
     */
    /**
     * Creates a dedicated dimensions analysis sheet using facade pattern
     */
    private void createDimensionsSheet(Workbook workbook, Indicateur indicateur,
            org.apache.poi.ss.usermodel.CellStyle headerStyle,
            org.apache.poi.ss.usermodel.CellStyle borderStyle) {
        Sheet dimensionsSheet = workbook.createSheet("Analyse des Dimensions");

        indicateurExportExcelMetaDataService.createDimensionsTable(dimensionsSheet, indicateur, 0, headerStyle,
                borderStyle);

        ExcelUtils.autoSizeColumns(dimensionsSheet);
    }

    /**
     * Creates flat data sheet
     */
    private void createFlatDataSheet(Workbook workbook, Indicateur indicateur) {
        Sheet flatSheet = workbook.createSheet("Données");
        var flatData = IndicateurFlatDataTable.buildFlatTableData(indicateur);
        for (int i = 0; i < flatData.size(); i++) {
            var row = flatSheet.createRow(i);
            List<String> rowData = flatData.get(i);
            for (int j = 0; j < rowData.size(); j++) {
                String value = rowData.get(j);
                var cell = row.createCell(j);
                ExcelUtils.setCellValueAutoType(cell, value);
            }
        }
        ExcelUtils.autoSizeColumns(flatSheet);
    }

    /**
     * Creates pivot data sheet
     */
    private void createPivotDataSheet(Workbook workbook, Indicateur indicateur) {
        Sheet pivotSheet = workbook.createSheet("Tableau croisé");
        var pivotData = IndicateurPivotDataTable.buildPivotTableData(indicateur);
        for (int i = 0; i < pivotData.size(); i++) {
            var row = pivotSheet.createRow(i);
            List<String> rowData = pivotData.get(i);
            for (int j = 0; j < rowData.size(); j++) {
                String value = rowData.get(j);
                var cell = row.createCell(j);
                ExcelUtils.setCellValueAutoType(cell, value);
            }
        }
        ExcelUtils.autoSizeColumns(pivotSheet);
    }

    /**
     * Check if pivot data should be included based on data table type
     */
    private boolean shouldIncludePivotData(IndicateurExportRequestDto.DataTableType dataTableType) {
        return dataTableType == IndicateurExportRequestDto.DataTableType.PIVOT ||
                dataTableType == IndicateurExportRequestDto.DataTableType.BOTH;
    }

    /**
     * Check if flat data should be included based on data table type
     */
    private boolean shouldIncludeFlatData(IndicateurExportRequestDto.DataTableType dataTableType) {
        return dataTableType == IndicateurExportRequestDto.DataTableType.FLAT ||
                dataTableType == IndicateurExportRequestDto.DataTableType.BOTH;
    }

    // ...existing code... (keep existing private methods)
}
