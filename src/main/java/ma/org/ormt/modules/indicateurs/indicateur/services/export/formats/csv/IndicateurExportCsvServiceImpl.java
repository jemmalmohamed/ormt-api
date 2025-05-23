package ma.org.ormt.modules.indicateurs.indicateur.services.export.formats.csv;

import ma.org.ormt.core.utilities.CsvUtils;
import ma.org.ormt.modules.indicateurs.indicateur.dtos.export.IndicateurExportRequest;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.helpers.IndicateurFlatDataTable;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.helpers.IndicateurMetaDataTable;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.helpers.IndicateurPivotDataTable;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;
import java.io.IOException;
import java.util.List;

@Service
public class IndicateurExportCsvServiceImpl implements IndicateurExportCsvService {
    @Override
    public ResponseEntity<byte[]> export(Indicateur indicateur, IndicateurExportRequest request) throws IOException {
        if (indicateur == null) {
            return ResponseEntity.badRequest().body(new byte[0]);
        }
        StringBuilder csvBuilder = new StringBuilder();
        if (request.isMetaDataSheet()) {
            csvBuilder.append("Informations\n");
            List<List<String>> metaData = IndicateurMetaDataTable.buildMetaTableData(indicateur);
            for (List<String> row : metaData) {
                csvBuilder.append(CsvUtils.rowToCsv(row)).append("\n");
            }
            csvBuilder.append("\n");
        }
        if (request.isPivotTableSheet()) {
            csvBuilder.append("Tableau croisé\n");
            List<List<String>> pivotData = IndicateurPivotDataTable.buildPivotSheetData(indicateur);
            for (List<String> row : pivotData) {
                csvBuilder.append(CsvUtils.rowToCsv(row)).append("\n");
            }
            csvBuilder.append("\n");
        }
        if (request.isFlatTableSheet()) {
            csvBuilder.append("Données\n");
            List<List<String>> flatData = IndicateurFlatDataTable.buildFlatTableData(indicateur);
            for (List<String> row : flatData) {
                csvBuilder.append(CsvUtils.rowToCsv(row)).append("\n");
            }
        }
        byte[] csvBytes = csvBuilder.toString().getBytes("UTF-8");
        byte[] output = CsvUtils.addUtf8Bom(csvBytes);
        String safeFileName = indicateur.getNom().replaceAll("[^\\p{ASCII}]", "_") + ".csv";
        return CsvUtils.exportCsvFormat(output, safeFileName);
    }
}
