package ma.org.ormt.modules.indicateurs.indicateur.services.export.data.excel;

import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Service;

import ma.org.ormt.core.utilities.ExcelUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * Service spécialisé pour la construction de feuilles Excel avec des tables de
 * données
 * Sépare la logique de construction Excel de la logique métier
 */
@Service
@Slf4j
public class ExcelDataTableBuilder {

    /**
     * Crée une table de données dans une feuille Excel
     */
    public int createDataTableInSheet(Sheet sheet,
            List<List<String>> tableData,
            String tableTitle,
            int startRowIdx,
            org.apache.poi.ss.usermodel.CellStyle headerStyle,
            org.apache.poi.ss.usermodel.CellStyle borderStyle) {

        if (sheet == null || tableData == null || tableData.isEmpty()) {
            log.warn("Paramètres invalides pour la création de table de données Excel");
            return startRowIdx;
        }

        try {
            // Titre de la section si fourni
            if (tableTitle != null && !tableTitle.trim().isEmpty()) {
                Row titleRow = sheet.createRow(startRowIdx++);
                ExcelUtils.createCell(titleRow, 0, tableTitle, headerStyle);
            }

            // Créer les lignes de données
            for (List<String> rowData : tableData) {
                Row dataRow = sheet.createRow(startRowIdx++);
                for (int i = 0; i < rowData.size() && i < 50; i++) { // Limiter à 50 colonnes pour éviter les problèmes
                    String cellValue = rowData.get(i) != null ? rowData.get(i) : "";
                    ExcelUtils.createCellAutoType(dataRow, i, cellValue, borderStyle);
                }
            }

            return startRowIdx;

        } catch (Exception e) {
            log.error("Erreur lors de la création de la table de données '{}': {}", tableTitle, e.getMessage(), e);
            return startRowIdx;
        }
    }

    /**
     * Crée une table avec un message d'absence de données
     */
    public int createNoDataMessage(Sheet sheet,
            String message,
            int startRowIdx,
            org.apache.poi.ss.usermodel.CellStyle style) {

        if (sheet == null) {
            return startRowIdx;
        }

        try {
            Row noDataRow = sheet.createRow(startRowIdx++);
            String displayMessage = message != null ? message : "Aucune donnée disponible";
            ExcelUtils.createCell(noDataRow, 0, displayMessage, style);
            return startRowIdx;
        } catch (Exception e) {
            log.error("Erreur lors de la création du message d'absence de données: {}", e.getMessage(), e);
            return startRowIdx;
        }
    }

    /**
     * Estime le nombre de colonnes nécessaires pour une table
     */
    public int estimateColumnCount(List<List<String>> tableData) {
        if (tableData == null || tableData.isEmpty()) {
            return 0;
        }

        return tableData.stream()
                .mapToInt(List::size)
                .max()
                .orElse(0);
    }

    /**
     * Valide qu'une table de données est exportable
     */
    public boolean isTableExportable(List<List<String>> tableData) {
        return tableData != null &&
                !tableData.isEmpty() &&
                tableData.stream().anyMatch(row -> !row.isEmpty());
    }

    /**
     * Nettoie les données d'une table pour l'export Excel
     */
    public List<List<String>> sanitizeTableData(List<List<String>> tableData) {
        if (tableData == null) {
            return List.of();
        }

        return tableData.stream()
                .map(row -> row.stream()
                        .map(this::sanitizeCellValue)
                        .toList())
                .toList();
    }

    /**
     * Nettoie la valeur d'une cellule
     */
    private String sanitizeCellValue(String value) {
        if (value == null) {
            return "";
        }

        // Enlever les caractères de contrôle qui peuvent causer des problèmes dans
        // Excel
        return value.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "").trim();
    }
}
