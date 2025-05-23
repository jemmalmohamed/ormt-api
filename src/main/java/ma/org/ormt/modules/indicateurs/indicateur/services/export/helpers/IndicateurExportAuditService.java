package ma.org.ormt.modules.indicateurs.indicateur.services.export.helpers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import ma.org.ormt.core.utilities.ExcelUtils;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;

@Service
public class IndicateurExportAuditService {

    @Autowired
    private IndicateurExportMetaDataCreationService indicateurExportTableCreationService;

    // Enum pour centraliser la gestion des colonnes et des titres
    private enum IndicateurColumn {
        ESPACES("Espaces"),
        DOMAINES("Domaines"),
        SOUS_DOMAINES("Sous domaines"),

        ID("id"),
        INDICATEUR("Indicateur"),
        UNITE("Unité"),
        CATEGORIE("Catégorie"),
        SOURCE("Source"),
        ABREVIATION("Abréviation"),
        TYPE_TB("Type TB"),
        TYPE_GRAPHE("Type Graphe"),
        DESCRIPTION("Description"),
        REGLE_CALCUL("Règle de calcul"),
        ACTIF("Actif"),
        HAS_DATA("A des données");

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
                ExcelUtils.createCell(header, i, columns[i].getHeader());
            }
            // Data
            int rowIdx = 1;
            for (Indicateur indicateur : indicateurs) {
                Row row = sheet.createRow(rowIdx++);
                for (int i = 0; i < columns.length; i++) {
                    switch (columns[i]) {
                        case ESPACES:
                            IndicateurExportCellCreationService.createEspacesCell(row, i, indicateur);
                            break;
                        case DOMAINES:
                            IndicateurExportCellCreationService.createDomainesCell(row, i, indicateur);
                            break;
                        case SOUS_DOMAINES:
                            IndicateurExportCellCreationService.createSousDomainesCell(row, i, indicateur);
                            break;
                        case ID:
                            ExcelUtils.createCell(row, i, indicateur.getId());
                            break;
                        case INDICATEUR:
                            ExcelUtils.createCell(row, i, indicateur.getNom());
                            break;
                        case DESCRIPTION:
                            ExcelUtils.createCell(row, i, indicateur.getDescription());
                            break;
                        case ABREVIATION:
                            ExcelUtils.createCell(row, i, indicateur.getAbreviation());
                            break;
                        case CATEGORIE:
                            ExcelUtils.createCell(row, i, indicateur.getCategorie());
                            break;
                        case ACTIF:
                            ExcelUtils.createCell(row, i,
                                    indicateur.getActif() != null ? (indicateur.getActif() ? "Oui" : "Non") : "");
                            break;
                        case TYPE_GRAPHE:
                            ExcelUtils.createCell(row, i, indicateur.getTypeGraphe());
                            break;
                        case TYPE_TB:
                            ExcelUtils.createCell(row, i, indicateur.getTypeTb());
                            break;
                        case UNITE:
                            ExcelUtils.createCell(row, i, indicateur.getUnite());
                            break;
                        case REGLE_CALCUL:
                            ExcelUtils.createCell(row, i, indicateur.getRegleCalcul());
                            break;
                        case SOURCE:
                            ExcelUtils.createCell(row, i, indicateur.getSource().getNom());
                            break;
                        case HAS_DATA:
                            int nbDonnees = indicateur.getDonnees() != null ? indicateur.getDonnees().size() : 0;
                            String aDonnees = nbDonnees > 0 ? "Oui" : "Non";
                            ExcelUtils.createCell(row, i, aDonnees);
                            break;
                        // Ajouter d'autres colonnes ici si besoin
                    }
                }
            }
            ExcelUtils.autoSizeColumns(sheet, columns.length);
            workbook.write(out);
            return ExcelUtils.exportExcelFormat(out, "indicateurs");
        }
    }

    public ResponseEntity<byte[]> exportIndicateursParSheet(List<Indicateur> indicateurs) throws IOException {
        if (indicateurs == null || indicateurs.isEmpty()) {
            return ExcelUtils.exportExcelFormat(new ByteArrayOutputStream(), "indicateurs-details");
        }
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            java.util.Set<String> usedSheetNames = new java.util.HashSet<>();
            var borderStyle = ExcelUtils.createBorderedStyle(workbook);
            var headerStyle = ExcelUtils.createHeaderStyle(workbook);
            for (Indicateur indicateur : indicateurs) {
                String baseName = indicateur.getNom() != null ? indicateur.getNom()
                        : ("Indicateur_" + indicateur.getId());
                String sheetName = baseName.length() > 31 ? baseName.substring(0, 31) : baseName;
                String originalSheetName = sheetName;
                int suffix = 1;
                while (usedSheetNames.contains(sheetName)) {
                    String suffixStr = "_" + suffix;
                    int maxLen = 31 - suffixStr.length();
                    sheetName = (originalSheetName.length() > maxLen ? originalSheetName.substring(0, maxLen)
                            : originalSheetName) + suffixStr;
                    suffix++;
                }
                usedSheetNames.add(sheetName);
                Sheet sheet = workbook.createSheet(sheetName);
                int rowIdx = 0;
                rowIdx = indicateurExportTableCreationService.createMetaTable(sheet, indicateur, rowIdx, headerStyle,
                        borderStyle);
                rowIdx++; // Saut de ligne
                rowIdx = indicateurExportTableCreationService.createDomainesTable(sheet, indicateur, rowIdx,
                        headerStyle, borderStyle);
                rowIdx++; // Saut de ligne
                rowIdx = indicateurExportTableCreationService.createDimensionsTable(sheet, indicateur, rowIdx,
                        headerStyle, borderStyle);
                rowIdx++; // Saut de ligne
                // rowIdx = createDimensionsTable(sheet, indicateur, rowIdx, headerStyle,
                // borderStyle);
                ExcelUtils.autoSizeColumns(sheet, 6);
            }
            workbook.write(out);
            return ExcelUtils.exportExcelFormat(out, "indicateurs-details");
        }
    }
}
