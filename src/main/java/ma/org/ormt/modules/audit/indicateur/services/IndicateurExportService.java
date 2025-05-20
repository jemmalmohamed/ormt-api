package ma.org.ormt.modules.audit.indicateur.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import ma.org.ormt.core.utilities.XlsUtils;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;

@Service
public class IndicateurExportService {

    private void createCell(Row row, int colIdx, String value) {
        var cell = row.createCell(colIdx);
        cell.setCellValue(value);
        // Personnalisation possible: cell.setCellStyle(...)
    }

    private void createSousDomainesCell(Row row, int colIdx, Indicateur indicateur) {
        String sousDomaines = indicateur.getSousDomaines() != null
                ? indicateur.getSousDomaines().stream()
                        .map(sd -> sd.getNom() != null ? sd.getNom() : "")
                        .filter(nom -> !nom.isEmpty())
                        .reduce((a, b) -> a + ", " + b).orElse("")
                : "";
        createCell(row, colIdx, sousDomaines);
        // Personnalisation possible: row.getCell(colIdx).setCellStyle(...)
    }

    private void createDomainesCell(Row row, int colIdx, Indicateur indicateur) {
        String domaines = indicateur.getSousDomaines() != null
                ? indicateur.getSousDomaines().stream()
                        .map(sd -> sd.getDomaine() != null && sd.getDomaine().getNom() != null
                                ? sd.getDomaine().getNom()
                                : "")
                        .distinct()
                        .filter(nom -> !nom.isEmpty())
                        .reduce((a, b) -> a + ", " + b).orElse("")
                : "";
        createCell(row, colIdx, domaines);
    }

    // Enum pour centraliser la gestion des colonnes et des titres
    private enum IndicateurColumn {
        DOMAINES("Domaines"),
        SOUS_DOMAINES("Sous domaines"),
        NOM("Nom"),
        DESCRIPTION("Description"),
        ABREVIATION("Abréviation"),
        CATEGORIE("Catégorie"),
        ACTIF("Actif"),
        TYPE_GRAPHE("Type Graphe"),
        TYPE_TB("Type TB"),
        UNITE("Unité"),
        REGLE_CALCUL("Règle de calcul");

        private final String header;

        IndicateurColumn(String header) {
            this.header = header;
        }

        public String getHeader() {
            return header;
        }
    }

    public ResponseEntity<byte[]> exportIndicateursAudit(List<Indicateur> indicateurs) throws IOException {
        IndicateurColumn[] columns = IndicateurColumn.values();
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Indicateurs");
            // Header
            Row header = sheet.createRow(0);
            for (int i = 0; i < columns.length; i++) {
                createCell(header, i, columns[i].getHeader());
            }
            // Data
            int rowIdx = 1;
            for (Indicateur indicateur : indicateurs) {
                Row row = sheet.createRow(rowIdx++);
                for (int i = 0; i < columns.length; i++) {
                    switch (columns[i]) {
                        case DOMAINES:
                            createDomainesCell(row, i, indicateur);
                            break;
                        case SOUS_DOMAINES:
                            createSousDomainesCell(row, i, indicateur);
                            break;
                        case NOM:
                            createCell(row, i, indicateur.getNom());
                            break;
                        case DESCRIPTION:
                            createCell(row, i, indicateur.getDescription());
                            break;
                        case ABREVIATION:
                            createCell(row, i, indicateur.getAbreviation());
                            break;
                        case CATEGORIE:
                            createCell(row, i, indicateur.getCategorie());
                            break;
                        case ACTIF:
                            createCell(row, i,
                                    indicateur.getActif() != null ? (indicateur.getActif() ? "Oui" : "Non") : "");
                            break;
                        case TYPE_GRAPHE:
                            createCell(row, i, indicateur.getTypeGraphe());
                            break;
                        case TYPE_TB:
                            createCell(row, i, indicateur.getTypeTb());
                            break;
                        case UNITE:
                            createCell(row, i, indicateur.getUnite());
                            break;
                        case REGLE_CALCUL:
                            createCell(row, i, indicateur.getRegleCalcul());
                            break;
                        // Ajouter d'autres colonnes ici si besoin
                    }
                }
            }
            XlsUtils.autoSizeColumns(sheet, columns.length);
            workbook.write(out);
            return XlsUtils.exportExcelFormat(out, "indicateurs");
        }
    }
}
