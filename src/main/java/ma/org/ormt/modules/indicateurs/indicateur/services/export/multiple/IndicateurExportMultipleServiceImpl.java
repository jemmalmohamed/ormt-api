package ma.org.ormt.modules.indicateurs.indicateur.services.export.multiple;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import ma.org.ormt.core.utilities.ExcelUtils;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.dtos.IndicateurExportRequestDto;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.helpers.IndicateurExportDetailedService;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.helpers.IndicateurExportSimpleService;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.helpers.utils.IndicateurExportFilterService;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.helpers.utils.IndicateurExportUtilService;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.helpers.utils.IndicateurExportUtilService.IndicateurColumn;
import lombok.extern.slf4j.Slf4j;

/**
 * Service principal orchestrant les exports d'indicateurs
 * Utilise des services spécialisés pour chaque type d'export
 * 
 * REFACTORISÉ pour respecter les bonnes pratiques:
 * - Single Responsibility Principle
 * - Délégation à des services spécialisés
 * - Code plus maintenable et testable
 */
@Service
@Slf4j
public class IndicateurExportMultipleServiceImpl implements IndicateurExportMultipleService {

    @Autowired
    private IndicateurExportFilterService filterService;

    @Autowired
    private IndicateurExportSimpleService simpleExportService;

    @Autowired
    private IndicateurExportDetailedService detailedExportService;

    @Autowired
    private IndicateurExportUtilService utilService;

    /**
     * Export simple pour compatibilité
     */
    @Override
    public ResponseEntity<byte[]> exportIndicateursAudit(List<Indicateur> indicateurs) throws IOException {
        return exportIndicateursWithOptions(indicateurs, IndicateurExportRequestDto.builder().build());
    }

    /**
     * Exporte les indicateurs selon les options spécifiées (format tableau simple)
     */
    @Override
    public ResponseEntity<byte[]> exportIndicateursWithOptions(List<Indicateur> indicateurs,
            IndicateurExportRequestDto exportRequest) throws IOException {

        if (indicateurs == null || indicateurs.isEmpty()) {
            log.warn("Tentative d'export avec une liste d'indicateurs vide ou null");
            return ExcelUtils.exportExcelFormat(new ByteArrayOutputStream(),
                    exportRequest.getFileName() != null ? exportRequest.getFileName() : "indicateurs-empty");
        }

        try {
            // Filtrer les indicateurs
            List<Indicateur> filteredIndicateurs = filterService.filterIndicateurs(indicateurs, exportRequest);

            if (filterService.isEmpty(filteredIndicateurs)) {
                log.warn("Aucun indicateur trouvé après filtrage");
                return ExcelUtils.exportExcelFormat(new ByteArrayOutputStream(),
                        exportRequest.getFileName() + "-empty");
            }

            // Déterminer les colonnes à exporter
            List<IndicateurColumn> columnsToExport = utilService.getColumnsToExport(exportRequest.getColumnsToExport());

            // Choisir la méthode d'export selon le type de groupement
            switch (exportRequest.getGroupBy()) {
                case BY_DOMAINE:
                    return exportByDomaine(filteredIndicateurs, columnsToExport, exportRequest.getFileName());
                case BY_SOURCE:
                    return exportBySource(filteredIndicateurs, columnsToExport, exportRequest.getFileName());
                default:
                    return simpleExportService.exportAllInSingleSheet(filteredIndicateurs, columnsToExport,
                            exportRequest.getFileName());
            }
        } catch (Exception e) {
            log.error("Erreur lors de l'export des indicateurs: {}", e.getMessage(), e);
            throw new IOException("Erreur lors de la génération de l'export: " + e.getMessage(), e);
        }
    }

    /**
     * Export groupé par domaine
     */
    private ResponseEntity<byte[]> exportByDomaine(List<Indicateur> indicateurs,
            List<IndicateurColumn> columns, String fileName) throws IOException {

        var groupedByDomaine = filterService.groupByDomaine(indicateurs);
        return simpleExportService.createMultiSheetWorkbook(groupedByDomaine, columns,
                fileName + "-par-domaine", utilService);
    }

    /**
     * Export groupé par source
     */
    private ResponseEntity<byte[]> exportBySource(List<Indicateur> indicateurs,
            List<IndicateurColumn> columns, String fileName) throws IOException {

        var groupedBySource = filterService.groupBySource(indicateurs);
        return simpleExportService.createMultiSheetWorkbook(groupedBySource, columns,
                fileName + "-par-source", utilService);
    }

    /**
     * Export détaillé avec options (un sheet par indicateur)
     */
    @Override
    public ResponseEntity<byte[]> exportIndicateursParSheetWithOptions(List<Indicateur> indicateurs,
            IndicateurExportRequestDto exportRequest) throws IOException {

        if (indicateurs == null || indicateurs.isEmpty()) {
            log.warn("Tentative d'export avec une liste d'indicateurs vide ou null");
            return ExcelUtils.exportExcelFormat(new ByteArrayOutputStream(),
                    exportRequest.getFileName() != null ? exportRequest.getFileName() : "indicateurs-details-empty");
        }

        try {
            // Filtrer les indicateurs
            List<Indicateur> filteredIndicateurs = filterService.filterIndicateurs(indicateurs, exportRequest);

            if (filterService.isEmpty(filteredIndicateurs)) {
                log.warn("Aucun indicateur trouvé après filtrage");
                return ExcelUtils.exportExcelFormat(new ByteArrayOutputStream(),
                        exportRequest.getFileName() + "-empty");
            }

            // Déterminer les sections à exporter
            List<String> sectionsToExport = utilService.getSectionsToExport(exportRequest.getSectionsToExport());

            return detailedExportService.createDetailedWorkbook(filteredIndicateurs, sectionsToExport, exportRequest);

        } catch (Exception e) {
            log.error("Erreur lors de l'export détaillé des indicateurs: {}", e.getMessage(), e);
            throw new IOException("Erreur lors de la génération de l'export détaillé: " + e.getMessage(), e);
        }
    }

}
