package ma.org.ormt.modules.indicateurs.indicateur.services.export.data.excel;

import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.data.builders.DataTableBuilderService;
import lombok.extern.slf4j.Slf4j;

/**
 * Service responsable de la création des tables de données (pivot/flat) pour
 * l'export
 * REFACTORISÉ pour utiliser les builders organisés
 */
@Service
@Slf4j
public class IndicateurExcelExportDataTableService {

        @Autowired
        private DataTableBuilderService dataTableBuilderService;

        @Autowired
        private ExcelDataTableBuilder excelDataTableBuilder;

        /**
         * Crée une table de données au format pivot
         */
        public int createPivotDataTable(Sheet sheet, Indicateur indicateur, int rowIdx,
                        org.apache.poi.ss.usermodel.CellStyle headerStyle,
                        org.apache.poi.ss.usermodel.CellStyle borderStyle) {

                // Construire les données pivot
                List<List<String>> pivotData = dataTableBuilderService.buildDataTable(
                                indicateur, DataTableBuilderService.DataTableType.PIVOT);

                if (pivotData.isEmpty()) {
                        return excelDataTableBuilder.createNoDataMessage(sheet,
                                        "Aucune donnée pivot disponible", rowIdx, borderStyle);
                }

                // Créer la table dans Excel
                return excelDataTableBuilder.createDataTableInSheet(sheet, pivotData,
                                "Données (Format Pivot)", rowIdx, headerStyle, borderStyle);
        }

        /**
         * Crée une table de données au format plat
         */
        public int createFlatDataTable(Sheet sheet, Indicateur indicateur, int rowIdx,
                        org.apache.poi.ss.usermodel.CellStyle headerStyle,
                        org.apache.poi.ss.usermodel.CellStyle borderStyle) {

                // Construire les données plates
                List<List<String>> flatData = dataTableBuilderService.buildDataTable(
                                indicateur, DataTableBuilderService.DataTableType.FLAT);

                if (flatData.isEmpty()) {
                        return excelDataTableBuilder.createNoDataMessage(sheet,
                                        "Aucune donnée plate disponible", rowIdx, borderStyle);
                }

                // Créer la table dans Excel
                return excelDataTableBuilder.createDataTableInSheet(sheet, flatData,
                                "Données (Format Plat)", rowIdx, headerStyle, borderStyle);
        }

        /**
         * Crée une table de données CRUD
         */
        public int createCrudDataTable(Sheet sheet, Indicateur indicateur, int rowIdx,
                        org.apache.poi.ss.usermodel.CellStyle headerStyle,
                        org.apache.poi.ss.usermodel.CellStyle borderStyle) {

                // Construire les données CRUD
                List<List<String>> crudData = dataTableBuilderService.buildDataTable(
                                indicateur, DataTableBuilderService.DataTableType.CRUD);

                if (crudData.isEmpty()) {
                        return excelDataTableBuilder.createNoDataMessage(sheet,
                                        "Aucune donnée CRUD disponible", rowIdx, borderStyle);
                }

                // Créer la table dans Excel
                return excelDataTableBuilder.createDataTableInSheet(sheet, crudData,
                                "Données (Format CRUD)", rowIdx, headerStyle, borderStyle);
        }

        /**
         * Vérifie si un indicateur a des données
         */
        public boolean hasData(Indicateur indicateur) {
                return dataTableBuilderService.hasValidDataForTable(indicateur);
        }

        /**
         * Compte le nombre de données pour un indicateur
         */
        public int countData(Indicateur indicateur) {
                return dataTableBuilderService.getDataRowCount(indicateur);
        }

        /**
         * Vérifie si un indicateur peut produire une table pivot
         */
        public boolean canBuildPivotTable(Indicateur indicateur) {
                return dataTableBuilderService.canBuildPivotTable(indicateur);
        }
}
