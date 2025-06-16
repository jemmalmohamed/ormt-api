package ma.org.ormt.modules.indicateurs.indicateur.services.export.helpers;

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
import ma.org.ormt.modules.indicateurs.indicateur.services.export.data.excel.IndicateurExcelExportDataTableService;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.dtos.IndicateurExportRequestDto;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.helpers.utils.IndicateurExportUtilService;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.meta.excel.IndicateurExportExcelMetaDataService;
import lombok.extern.slf4j.Slf4j;

/**
 * Service responsable des exports détaillés d'indicateurs (un sheet par
 * indicateur)
 */
@Service
@Slf4j
public class IndicateurExportDetailedService {

    @Autowired
    private IndicateurExportExcelMetaDataService metaDataCreationService;

    @Autowired
    private IndicateurExcelExportDataTableService dataTableService;

    @Autowired
    private IndicateurExportUtilService utilService;

    /**
     * Crée un workbook avec des feuilles détaillées pour chaque indicateur
     */
    public ResponseEntity<byte[]> createDetailedWorkbook(List<Indicateur> indicateurs, List<String> sections,
            IndicateurExportRequestDto exportRequest) throws IOException {

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            java.util.Set<String> usedSheetNames = new java.util.HashSet<>();
            var borderStyle = ExcelUtils.createBorderedStyle(workbook);
            var headerStyle = ExcelUtils.createHeaderStyle(workbook);

            for (Indicateur indicateur : indicateurs) {
                if (indicateur == null) {
                    log.warn("Indicateur null trouvé dans la liste, ignoré");
                    continue;
                }

                // Créer nom de feuille unique
                String baseName = indicateur.getNom() != null ? indicateur.getNom()
                        : ("Indicateur_" + indicateur.getId());
                String sheetName = utilService.cleanSheetName(baseName);
                sheetName = utilService.ensureUniqueSheetName(sheetName, usedSheetNames);
                usedSheetNames.add(sheetName);

                Sheet sheet = workbook.createSheet(sheetName);
                int rowIdx = 0;

                // Créer les sections demandées
                for (String section : sections) {
                    rowIdx = createSectionForIndicateur(sheet, indicateur, section, rowIdx, headerStyle, borderStyle,
                            exportRequest);
                    rowIdx++; // Saut de ligne entre sections
                }

                ExcelUtils.autoSizeColumns(sheet, 6);
            }

            workbook.write(out);
            return ExcelUtils.exportExcelFormat(out, exportRequest.getFileName() + "-details");
        }
    }

    /**
     * Crée une section spécifique pour un indicateur
     */
    private int createSectionForIndicateur(Sheet sheet, Indicateur indicateur, String section, int rowIdx,
            org.apache.poi.ss.usermodel.CellStyle headerStyle, org.apache.poi.ss.usermodel.CellStyle borderStyle,
            IndicateurExportRequestDto exportRequest) {

        try {
            switch (section.toUpperCase()) {
                case "META":
                    return metaDataCreationService.createMetaTable(sheet, indicateur, rowIdx,
                            headerStyle, borderStyle, exportRequest);

                case "CONFIGURATION":
                    return metaDataCreationService.createConfigurationTable(sheet, indicateur, rowIdx,
                            headerStyle, borderStyle);

                case "DOMAINES":
                    return metaDataCreationService.createDomainesTable(sheet, indicateur, rowIdx,
                            headerStyle, borderStyle);

                case "DIMENSIONS":
                    return metaDataCreationService.createDimensionsTable(sheet, indicateur, rowIdx,
                            headerStyle, borderStyle);

                case "DATA_STATS":
                    if (exportRequest.isIncludeDataStats()) {
                        return metaDataCreationService.createDataStatsTable(sheet, indicateur, rowIdx,
                                headerStyle, borderStyle);
                    }
                    break;

                case "PIVOT_DATA":
                    if (utilService.shouldIncludeDataTable(exportRequest.getDataTableType(), "PIVOT")) {
                        return dataTableService.createPivotDataTable(sheet, indicateur, rowIdx, headerStyle,
                                borderStyle);
                    }
                    break;

                case "FLAT_DATA":
                    if (utilService.shouldIncludeDataTable(exportRequest.getDataTableType(), "FLAT")) {
                        return dataTableService.createFlatDataTable(sheet, indicateur, rowIdx, headerStyle,
                                borderStyle);
                    }
                    break;

                default:
                    log.warn("Section inconnue demandée: {}", section);
                    break;
            }
        } catch (Exception e) {
            log.error("Erreur lors de la création de la section {} pour l'indicateur {}: {}",
                    section, indicateur.getId(), e.getMessage());
        }

        return rowIdx;
    }
}
