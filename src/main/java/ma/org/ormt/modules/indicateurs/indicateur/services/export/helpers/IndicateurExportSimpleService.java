package ma.org.ormt.modules.indicateurs.indicateur.services.export.helpers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;
import ma.org.ormt.core.utilities.ExcelUtils;
import ma.org.ormt.modules.indicateurs.dimension.models.Dimension;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.helpers.utils.IndicateurExportUtilService;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.helpers.utils.IndicateurExportUtilService.IndicateurColumn;

/**
 * Service responsable des exports d'indicateurs au format tableau simple
 */
@Service
@Slf4j
public class IndicateurExportSimpleService {

    /**
     * Export dans une seule feuille
     */
    public ResponseEntity<byte[]> exportAllInSingleSheet(List<Indicateur> indicateurs,
            List<IndicateurColumn> columns, String fileName) throws IOException {

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Indicateurs");

            // Header
            Row header = sheet.createRow(0);
            for (int i = 0; i < columns.size(); i++) {
                ExcelUtils.createCell(header, i, columns.get(i).getHeader());
            }

            // Data
            int rowIdx = 1;
            for (Indicateur indicateur : indicateurs) {
                if (indicateur == null) {
                    log.warn("Indicateur null trouvé dans la liste, ignoré");
                    continue;
                }

                Row row = sheet.createRow(rowIdx++);
                for (int i = 0; i < columns.size(); i++) {
                    createCellSafely(row, i, columns.get(i), indicateur);
                }
            }

            ExcelUtils.autoSizeColumns(sheet, columns.size());
            workbook.write(out);
            return ExcelUtils.exportExcelFormat(out, fileName != null ? fileName : "indicateurs");
        }
    }

    /**
     * Crée un workbook avec plusieurs feuilles groupées
     */
    public ResponseEntity<byte[]> createMultiSheetWorkbook(java.util.Map<String, List<Indicateur>> groupedData,
            List<IndicateurColumn> columns, String fileName, IndicateurExportUtilService utilService)
            throws IOException {

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            java.util.Set<String> usedSheetNames = new java.util.HashSet<>();

            for (java.util.Map.Entry<String, List<Indicateur>> entry : groupedData.entrySet()) {
                String groupName = entry.getKey();
                List<Indicateur> groupIndicateurs = entry.getValue();

                // Nettoyer le nom de la feuille
                String sheetName = utilService.cleanSheetName(groupName);
                sheetName = utilService.ensureUniqueSheetName(sheetName, usedSheetNames);
                usedSheetNames.add(sheetName);

                Sheet sheet = workbook.createSheet(sheetName);

                // Header
                Row header = sheet.createRow(0);
                for (int i = 0; i < columns.size(); i++) {
                    ExcelUtils.createCell(header, i, columns.get(i).getHeader());
                }

                // Data
                int rowIdx = 1;
                for (Indicateur indicateur : groupIndicateurs) {
                    if (indicateur == null)
                        continue;

                    Row row = sheet.createRow(rowIdx++);
                    for (int i = 0; i < columns.size(); i++) {
                        createCellSafely(row, i, columns.get(i), indicateur);
                    }
                }

                ExcelUtils.autoSizeColumns(sheet, columns.size());
            }

            workbook.write(out);
            return ExcelUtils.exportExcelFormat(out, fileName);
        }
    }

    /**
     * Crée une cellule de manière sécurisée en gérant les valeurs null
     */
    public void createCellSafely(Row row, int cellIndex, IndicateurColumn column, Indicateur indicateur) {
        try {
            switch (column) {
                case ESPACES:
                    createEspacesCell(row, cellIndex, indicateur);
                    break;
                case DOMAINES:
                    createDomainesCell(row, cellIndex, indicateur);
                    break;
                case SOUS_DOMAINES:
                    createSousDomainesCell(row, cellIndex, indicateur);
                    break;
                case ID:
                    ExcelUtils.createCell(row, cellIndex,
                            indicateur.getId() != null ? indicateur.getId().toString()
                                    : "");
                    break;
                case NOM:
                    ExcelUtils.createCell(row, cellIndex,
                            StringUtils.hasText(indicateur.getNom()) ? indicateur.getNom()
                                    : "");
                    break;
                case DESCRIPTION:
                    ExcelUtils.createCell(row, cellIndex,
                            StringUtils.hasText(indicateur.getDescription())
                                    ? indicateur.getDescription()
                                    : "");
                    break;
                case ABREVIATION:
                    ExcelUtils.createCell(row, cellIndex,
                            StringUtils.hasText(indicateur.getAbreviation())
                                    ? indicateur.getAbreviation()
                                    : "");
                    break;
                case CATEGORIE:
                    ExcelUtils.createCell(row, cellIndex,
                            StringUtils.hasText(indicateur.getCategorie())
                                    ? indicateur.getCategorie()
                                    : "");
                    break;
                case ACTIF:
                    String actifValue = indicateur.getActif() != null
                            ? (indicateur.getActif() ? "Oui" : "Non")
                            : "Non défini";
                    ExcelUtils.createCell(row, cellIndex, actifValue);
                    break;

                case TYPE_TB:
                    ExcelUtils.createCell(row, cellIndex,
                            StringUtils.hasText(indicateur.getTypeTb())
                                    ? indicateur.getTypeTb()
                                    : "");
                    break;
                case UNITE:
                    ExcelUtils.createCell(row, cellIndex,
                            StringUtils.hasText(indicateur.getUnite())
                                    ? indicateur.getUnite()
                                    : "");
                    break;
                case REGLE_CALCUL:
                    ExcelUtils.createCell(row, cellIndex,
                            StringUtils.hasText(indicateur.getRegleCalcul())
                                    ? indicateur.getRegleCalcul()
                                    : "");
                    break;
                case SOURCE:
                    String sourceName = "";
                    if (indicateur.getSource() != null
                            && StringUtils.hasText(indicateur.getSource().getAbreviation())) {
                        sourceName = indicateur.getSource().getAbreviation();
                    }
                    ExcelUtils.createCell(row, cellIndex, sourceName);
                    break;
                case HAS_DATA:
                    int nbDonnees = indicateur.getDonnees() != null ? indicateur.getDonnees().size()
                            : 0;
                    String aDonnees = nbDonnees > 0 ? "Oui (" + nbDonnees + ")" : "Non";
                    ExcelUtils.createCell(row, cellIndex, aDonnees);
                    break;
                case TERRITOIRE:
                    // Vérifier si l'indicateur est régional
                    String regionalStatus = analyzeTerritoireStatus(indicateur);
                    ExcelUtils.createCell(row, cellIndex, regionalStatus);

                    break;
                default:
                    ExcelUtils.createCell(row, cellIndex, "");
                    break;
            }
        } catch (Exception e) {
            log.error("Erreur lors de la création de la cellule pour la colonne {}: {}", column,
                    e.getMessage());
            ExcelUtils.createCell(row, cellIndex, "Erreur");
        }
    }

    public static void createEspacesCell(Row row, int colIdx, Indicateur indicateur) {
        String espaces = indicateur.getSousDomaines() != null
                ? indicateur.getSousDomaines().stream()
                        .filter(sd -> sd.getDomaine() != null
                                && sd.getDomaine().getEspaceDomaines() != null)
                        .flatMap(sd -> sd.getDomaine().getEspaceDomaines().stream())
                        .filter(ed -> ed.getEspace() != null && ed.getEspace().getNom() != null)
                        .map(ed -> ed.getEspace().getNom())
                        .distinct()
                        .filter(nom -> !nom.isEmpty())
                        .reduce((a, b) -> a + " - " + b).orElse("")
                : "";
        ExcelUtils.createCell(row, colIdx, espaces);
    }

    public static void createSousDomainesCell(Row row, int colIdx, Indicateur indicateur) {
        String sousDomaines = indicateur.getSousDomaines() != null
                ? indicateur.getSousDomaines().stream()
                        .map(sd -> sd.getNom() != null ? sd.getNom() : "")
                        .filter(nom -> !nom.isEmpty())
                        .reduce((a, b) -> a + ", " + b).orElse("")
                : "";
        ExcelUtils.createCell(row, colIdx, sousDomaines);
    }

    public static void createDomainesCell(Row row, int colIdx, Indicateur indicateur) {
        String domaines = indicateur.getSousDomaines() != null
                ? indicateur.getSousDomaines().stream()
                        .map(sd -> sd.getDomaine() != null && sd.getDomaine().getNom() != null
                                ? sd.getDomaine().getNom()
                                : "")
                        .distinct()
                        .filter(nom -> !nom.isEmpty())
                        .reduce((a, b) -> a + ", " + b).orElse("")
                : "";
        ExcelUtils.createCell(row, colIdx, domaines);
    }

    /**
     * Analyse le statut territorial d'un indicateur
     */
    private String analyzeTerritoireStatus(Indicateur indicateur) {
        // Récupérer les dimensions de l'indicateur
        List<Dimension> dimensions = indicateur.getDimensions();

        if (dimensions == null || dimensions.isEmpty()) {
            return "Pas de dimensions";
        }

        // Chercher une dimension de type "région" ou "territoire"
        boolean hasRegionDimension = dimensions.stream()
                .anyMatch(dimension -> dimension.getNom() != null &&
                        (dimension.getNom().toLowerCase().contains("region")));

        if (!hasRegionDimension) {
            return "National";
        }

        // Si on a une dimension région/territoire, vérifier les données
        if (indicateur.getDonnees() == null || indicateur.getDonnees().isEmpty()) {
            return "Régional mais pas de données";
        }

        // Récupérer toutes les valeurs pour les dimensions région de cet indicateur
        List<String> valeursRegionales = indicateur.getDonnees().stream()
                .flatMap(donnee -> donnee.getValeurDimensions().stream())
                .filter(vd -> vd.getDimension() != null &&
                        vd.getDimension().getNom() != null &&
                        vd.getDimension().getNom().toLowerCase().contains("region"))
                .map(vd -> vd.getValeur())
                .filter(valeur -> valeur != null && !valeur.trim().isEmpty())
                .map(valeur -> valeur.toLowerCase().trim())
                .distinct()
                .collect(Collectors.toList());

        if (valeursRegionales.isEmpty()) {
            return "Régional mais pas de données";
        }

        // Vérifier spécifiquement si Marrakech est présent
        boolean hasMarrakech = valeursRegionales.stream()
                .anyMatch(valeur -> valeur.contains("marrakech"));

        if (hasMarrakech) {
            // Marrakech trouvé - le mentionner en premier
            List<String> autresRegions = valeursRegionales.stream()
                    .filter(valeur -> !valeur.contains("marrakech"))
                    .filter(valeur -> Arrays.asList("casablanca", "rabat", "fès", "tanger",
                            "agadir", "meknès", "oujda", "kenitra", "tétouan",
                            "casablanca - settat", "rabat - salé - kénitra",
                            "fès - meknès", "tanger - tétouan - al hoceima").stream()
                            .anyMatch(region -> valeur.contains(region)))
                    .limit(2) // Limiter à 2 autres régions
                    .collect(Collectors.toList());

            String regionsText = "marrakech";
            if (!autresRegions.isEmpty()) {
                regionsText += ", " + String.join(", ", autresRegions);
                if (valeursRegionales.size() > autresRegions.size() + 1) {
                    regionsText += "...";
                }
            }

            return "Régional (" + regionsText + ")";
        } else {
            // Pas de Marrakech - vérifier autres régions marocaines
            List<String> regionsMarocaines = Arrays.asList(
                    "casablanca", "rabat", "fès", "tanger",
                    "agadir", "meknès", "oujda", "kenitra", "tétouan",
                    "casablanca - settat", "rabat - salé - kénitra",
                    "fès - meknès", "tanger - tétouan - al hoceima");

            List<String> regionsPresentes = valeursRegionales.stream()
                    .filter(valeur -> regionsMarocaines.stream()
                            .anyMatch(region -> valeur.contains(region)))
                    .collect(Collectors.toList());

            if (!regionsPresentes.isEmpty()) {
                String regionsText = regionsPresentes.stream()
                        .limit(3)
                        .collect(Collectors.joining(", "));

                if (regionsPresentes.size() > 3) {
                    regionsText += "...";
                }

                return "Régional (pas de données Marrakech, " + regionsText + ")";
            } else {
                return "Régional (pas de données Marrakech, autres territoires)";
            }
        }
    }
}
