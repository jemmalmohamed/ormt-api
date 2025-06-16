package ma.org.ormt.modules.indicateurs.indicateur.services.export.single.formats.xls;

import ma.org.ormt.core.utilities.ExcelUtils;
import ma.org.ormt.modules.indicateurs.indicateur.dtos.export.IndicateurExportRequest;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.data.builders.IndicateurFlatDataTable;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.data.builders.IndicateurPivotDataTable;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.meta.excel.IndicateurExportExcelMetaDataService;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class IndicateurExportExcelServiceImpl implements IndicateurExportExcelService {

    @Autowired
    private IndicateurExportExcelMetaDataService indicateurExportMetaDataCreationService;

    @Override
    public ResponseEntity<byte[]> export(Indicateur indicateur, IndicateurExportRequest request) throws IOException {
        if (indicateur == null) {
            return ResponseEntity.badRequest().body(new byte[0]);
        }
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            var borderStyle = ExcelUtils.createBorderedStyle(workbook);
            var headerStyle = ExcelUtils.createHeaderStyle(workbook);

            if (request.isMetaDataSheet()) {
                createMetadataSheet(workbook, indicateur, headerStyle, borderStyle, request);
            }

            if (request.isFlatTableSheet()) {
                createFlatDataSheet(workbook, indicateur);
            }

            if (request.isPivotTableSheet()) {
                createPivotDataSheet(workbook, indicateur);
            }

            workbook.write(out);
            String safeFileName = indicateur.getNom().replaceAll("[^\\p{ASCII}]", "_");
            return ExcelUtils.exportExcelFormat(out, safeFileName);
        }
    }

    /**
     * Creates metadata sheet with various detail levels based on request
     */
    private void createMetadataSheet(Workbook workbook, Indicateur indicateur,
            org.apache.poi.ss.usermodel.CellStyle headerStyle,
            org.apache.poi.ss.usermodel.CellStyle borderStyle,
            IndicateurExportRequest request) {

        // Create basic metadata sheet
        Sheet metaSheet = workbook.createSheet("Informations");

        // Determine which type of metadata to create based on data complexity
        boolean hasComplexDimensions = indicateur.getIndicateurDimensions() != null &&
                indicateur.getIndicateurDimensions().size() > 3;
        boolean hasSignificantData = indicateur.getDonnees() != null &&
                indicateur.getDonnees().size() > 100;

        if (hasComplexDimensions || hasSignificantData) {
            // Use detailed metadata for complex indicators
            indicateurExportMetaDataCreationService.createDetailedMetaTable(
                    metaSheet, indicateur, 0, headerStyle, borderStyle);
        } else {
            // Use basic metadata for simple indicators
            // indicateurExportMetaDataCreationService.createMetaTable(
            // metaSheet, indicateur, 0, headerStyle, borderStyle, request);
        }

        ExcelUtils.autoSizeColumns(metaSheet);

        // Optionally create additional specialized sheets
        if (hasComplexDimensions) {
            createDimensionsSheet(workbook, indicateur, headerStyle, borderStyle);
        }

        if (hasSignificantData) {
            createDataStatsSheet(workbook, indicateur, headerStyle, borderStyle);
        }
    }

    /**
     * Creates a dedicated dimensions analysis sheet using the original table format
     */
    private void createDimensionsSheet(Workbook workbook, Indicateur indicateur,
            org.apache.poi.ss.usermodel.CellStyle headerStyle,
            org.apache.poi.ss.usermodel.CellStyle borderStyle) {
        Sheet dimensionsSheet = workbook.createSheet("Analyse des Dimensions");
        // Use the original dimensions table format as requested
        indicateurExportMetaDataCreationService.createDimensionsTable(
                dimensionsSheet, indicateur, 0, headerStyle, borderStyle);
        ExcelUtils.autoSizeColumns(dimensionsSheet);
    }

    /**
     * Creates a dedicated data statistics sheet using the original format
     */
    private void createDataStatsSheet(Workbook workbook, Indicateur indicateur,
            org.apache.poi.ss.usermodel.CellStyle headerStyle,
            org.apache.poi.ss.usermodel.CellStyle borderStyle) {
        Sheet statsSheet = workbook.createSheet("Statistiques des Données");
        // Use the original data stats table format
        indicateurExportMetaDataCreationService.createDataStatsTable(
                statsSheet, indicateur, 0, headerStyle, borderStyle);
        ExcelUtils.autoSizeColumns(statsSheet);
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
}
