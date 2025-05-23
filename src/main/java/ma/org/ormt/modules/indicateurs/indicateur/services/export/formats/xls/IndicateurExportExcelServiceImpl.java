package ma.org.ormt.modules.indicateurs.indicateur.services.export.formats.xls;

import ma.org.ormt.core.utilities.ExcelUtils;
import ma.org.ormt.modules.indicateurs.indicateur.dtos.export.IndicateurExportRequest;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.helpers.IndicateurExportMetaDataCreationService;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.helpers.IndicateurFlatDataTable;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.helpers.IndicateurPivotDataTable;
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
    private IndicateurExportMetaDataCreationService indicateurExportMetaDataCreationService;

    @Override
    public ResponseEntity<byte[]> export(Indicateur indicateur, IndicateurExportRequest request) throws IOException {
        if (indicateur == null) {
            return ResponseEntity.badRequest().body(new byte[0]);
        }
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            var borderStyle = ExcelUtils.createBorderedStyle(workbook);
            var headerStyle = ExcelUtils.createHeaderStyle(workbook);

            if (request.isMetaDataSheet()) {
                Sheet metaSheet = workbook.createSheet("Informations");
                indicateurExportMetaDataCreationService.createMetaTable(metaSheet, indicateur, 0, headerStyle,
                        borderStyle);
                ExcelUtils.autoSizeColumns(metaSheet);
            }
            if (request.isFlatTableSheet()) {
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
            if (request.isPivotTableSheet()) {
                Sheet pivotSheet = workbook.createSheet("Tableau croisé");
                var pivotData = IndicateurPivotDataTable.buildPivotSheetData(indicateur);
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
            workbook.write(out);
            String safeFileName = indicateur.getNom().replaceAll("[^\\p{ASCII}]", "_");
            return ExcelUtils.exportExcelFormat(out, safeFileName);
        }
    }
}
