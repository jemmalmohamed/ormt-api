package ma.org.ormt.modules.indicateurs.indicateur.services.export.formats.pdf;

import ma.org.ormt.core.utilities.PdfExportUtils;
import ma.org.ormt.core.utilities.EnhancedPdfExportUtils;
import ma.org.ormt.modules.indicateurs.indicateur.dtos.export.IndicateurExportRequest;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.helpers.IndicateurFlatDataTable;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.helpers.IndicateurMetaDataTable;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.helpers.IndicateurPivotDataTable;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class IndicateurExportPdfServiceImpl implements IndicateurExportPdfService {
    @Override
    public ResponseEntity<byte[]> export(Indicateur indicateur, IndicateurExportRequest request) throws IOException {
        if (indicateur == null) {
            return ResponseEntity.badRequest().body(new byte[0]);
        }

        List<List<String>> metaData = null;
        List<List<String>> flatData = null;
        List<List<String>> pivotData = null;
        List<List<List<String>>> tables = new ArrayList<>();

        if (request.isMetaDataSheet()) {
            metaData = IndicateurMetaDataTable.buildMetaTableData(indicateur);
            tables.add(metaData);
        }
        if (request.isPivotTableSheet()) {
            pivotData = IndicateurPivotDataTable.buildPivotSheetData(indicateur);
            tables.add(pivotData);
        }
        if (request.isFlatTableSheet()) {
            flatData = IndicateurFlatDataTable.buildFlatTableData(indicateur);
            tables.add(flatData);
        }

        String safeFileName = indicateur.getNom().replaceAll("[^\\p{ASCII}]", "_") + ".pdf";

        // Utiliser l'utilitaire amélioré pour une meilleure mise en forme
        byte[] pdfBytes;
        if (tables.size() == 1) {
            // Si on n'a qu'une seule table, utiliser l'export optimisé spécialisé
            List<List<String>> singleTable = tables.get(0);
            if (request.isPivotTableSheet() && !request.isMetaDataSheet() && !request.isFlatTableSheet()) {
                // Export optimisé spécialement pour les tableaux pivot avec séparation
                // temporelle
                pdfBytes = EnhancedPdfExportUtils.generatePivotTablePdf(
                        "Tableau Pivot - " + indicateur.getNom(),
                        singleTable);
            } else {
                // Export optimisé général
                pdfBytes = EnhancedPdfExportUtils.generateOptimizedPdf("Export - " + indicateur.getNom(), singleTable);
            }
        } else {
            // Pour plusieurs tables, utiliser l'export multi-tables amélioré
            @SuppressWarnings("unchecked")
            List<List<String>>[] tablesArray = tables.toArray(new List[0]);
            pdfBytes = EnhancedPdfExportUtils.generateMultiTablePdf(
                    "Export Indicateur : " + indicateur.getNom(),
                    tablesArray,
                    safeFileName);
        }

        return PdfExportUtils.exportPdfFormat(pdfBytes, safeFileName);
    }
}
