package ma.org.ormt.modules.indicateurs.indicateur.services.export.meta.excel;

import java.util.List;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ma.org.ormt.core.utilities.ExcelUtils;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.dtos.IndicateurExportRequestDto;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.meta.facade.MetaDataCreationFacade;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.meta.models.MetaDataTable;

@Service
public class IndicateurExportExcelMetaDataService {

    @Autowired
    private MetaDataCreationFacade metaDataFacade;

    @Autowired
    private MetaDataExcelRenderer excelRenderer;

    /**
     * Creates a basic metadata table in Excel format
     */
    public int createMetaTable(Sheet sheet, Indicateur indicateur, int startRowIdx,
            CellStyle headerStyle, CellStyle borderStyle, IndicateurExportRequestDto exportRequest) {

        // Get columns to export from request (if specified)
        List<String> columnsToExport = exportRequest != null ? exportRequest.getColumnsToExport() : null;

        MetaDataTable metaTable = metaDataFacade.createSelectiveMetaData(indicateur, columnsToExport);
        return excelRenderer.renderMetaDataTable(sheet, metaTable, startRowIdx, headerStyle, borderStyle);
    }

    /**
     * Creates a detailed metadata table in Excel format
     */
    public int createDetailedMetaTable(Sheet sheet, Indicateur indicateur, int startRowIdx,
            CellStyle headerStyle, CellStyle borderStyle) {
        MetaDataTable metaTable = metaDataFacade.createDetailedMetaData(indicateur);
        return excelRenderer.renderMetaDataTable(sheet, metaTable, startRowIdx, headerStyle, borderStyle);
    }

    /**
     * Creates a dimensions-focused metadata table in Excel format
     */
    public int createDimensionsFocusedMetaTable(Sheet sheet, Indicateur indicateur, int startRowIdx,
            CellStyle headerStyle, CellStyle borderStyle) {
        MetaDataTable metaTable = metaDataFacade.createDimensionsFocusedMetaData(indicateur);
        return excelRenderer.renderMetaDataTable(sheet, metaTable, startRowIdx, headerStyle, borderStyle);
    }

    /**
     * Creates a data statistics focused metadata table in Excel format
     */
    public int createDataStatsFocusedMetaTable(Sheet sheet, Indicateur indicateur, int startRowIdx,
            CellStyle headerStyle, CellStyle borderStyle) {
        MetaDataTable metaTable = metaDataFacade.createDataStatsFocusedMetaData(indicateur);
        return excelRenderer.renderMetaDataTable(sheet, metaTable, startRowIdx, headerStyle, borderStyle);
    }

    // Legacy methods for backward compatibility

    /**
     * Original dimensions table format - maintained as requested
     * Creates dimensions table with the old layout (horizontal columns for each
     * dimension)
     */
    public int createDimensionsTable(Sheet sheet, Indicateur indicateur, int startRowIdx,
            CellStyle headerStyle, CellStyle borderStyle) {
        // Ajout d'un header global pour l'ensemble des dimensions
        Row globalHeader = sheet.createRow(startRowIdx++);
        // Crée un style de header avec fond gris clair
        CellStyle customHeaderStyle = sheet.getWorkbook().createCellStyle();
        customHeaderStyle.cloneStyleFrom(headerStyle);
        Font font = sheet.getWorkbook().createFont();
        font.setBold(true);
        customHeaderStyle.setFont(font);
        customHeaderStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        customHeaderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        ExcelUtils.createCell(globalHeader, 0, "Dimensions", customHeaderStyle);

        int maxRows = 0;
        int colIdx = 0;
        if (indicateur.getIndicateurDimensions() != null) {
            for (var indDim : indicateur.getIndicateurDimensions()) {
                int rowIdx = startRowIdx;
                String nom = indDim.getDimension() != null ? indDim.getDimension().getNom() : "";
                String libelle = indDim.getDimension() != null
                        && indDim.getDimension().getLibelle() != null
                                ? indDim.getDimension().getLibelle().toLowerCase()
                                : "";
                String principale = indDim.getPrincipale() != null ? (indDim.getPrincipale() ? "Oui" : "Non") : "";
                String temporelle = indDim.getTemporelle() != null ? (indDim.getTemporelle() ? "Oui" : "Non") : "";
                String description = indDim.getDimension() != null && indDim.getDimension().getDescription() != null
                        ? indDim.getDimension().getDescription().toLowerCase()
                        : "";

                java.util.Set<String> valeursSet = new java.util.LinkedHashSet<>();
                if (indicateur.getDonnees() != null) {
                    for (var donnee : indicateur.getDonnees()) {
                        if (donnee.getValeurDimensions() != null) {
                            for (var vd : donnee.getValeurDimensions()) {
                                if (vd.getDimension() != null && nom.equals(vd.getDimension().getNom())) {
                                    if (vd.getValeur() != null) {
                                        valeursSet.add(vd.getValeur());
                                    }
                                }
                            }
                        }
                    }
                }
                List<String> valeurs = new java.util.ArrayList<>(valeursSet);

                // Prépare les lignes du tableau vertical pour cette dimension
                List<String[]> dimRows = new java.util.ArrayList<>();
                dimRows.add(new String[] { "Nom", nom });
                dimRows.add(new String[] { "Libelle", libelle });
                dimRows.add(new String[] { "Principale", principale });
                dimRows.add(new String[] { "Temporelle", temporelle });
                dimRows.add(new String[] { "Description", description });

                for (int i = 0; i < dimRows.size(); i++) {
                    Row row = sheet.getRow(rowIdx) != null ? sheet.getRow(rowIdx) : sheet.createRow(rowIdx);
                    ExcelUtils.createCell(row, colIdx, dimRows.get(i)[0], headerStyle);
                    ExcelUtils.createCell(row, colIdx + 1, dimRows.get(i)[1], borderStyle);
                    rowIdx++;
                }

                // Ajout de la ligne "Valeurs possibles" fusionnée sur 2 colonnes, puis valeurs
                // en dessous
                if (!valeurs.isEmpty()) {
                    Row valeursHeaderRow = sheet.getRow(rowIdx) != null ? sheet.getRow(rowIdx)
                            : sheet.createRow(rowIdx);
                    ExcelUtils.createCell(valeursHeaderRow, colIdx, "Valeurs", headerStyle);
                    // Fusionne la cellule sur 2 colonnes
                    sheet.addMergedRegion(
                            new org.apache.poi.ss.util.CellRangeAddress(rowIdx, rowIdx, colIdx, colIdx + 1));
                    rowIdx++;
                    for (String val : valeurs) {
                        Row valRow = sheet.getRow(rowIdx) != null ? sheet.getRow(rowIdx) : sheet.createRow(rowIdx);
                        ExcelUtils.createCell(valRow, colIdx + 1, val, borderStyle);
                        rowIdx++;
                    }
                }
                int totalRows = (rowIdx - startRowIdx);
                maxRows = Math.max(maxRows, totalRows);
                colIdx += 2;
            }
        }
        return startRowIdx + (maxRows == 0 ? 1 : maxRows);
    }

    /**
     * Creates data stats table using the facade approach for consistency
     */
    public int createDataStatsTable(Sheet sheet, Indicateur indicateur, int rowIdx,
            CellStyle headerStyle, CellStyle borderStyle) {

        // Create data stats table using the facade
        MetaDataTable dataStatsTable = metaDataFacade.createDataStatsMetaData(indicateur);

        return excelRenderer.renderMetaDataTable(sheet, dataStatsTable, rowIdx, headerStyle, borderStyle);
    }

    /**
     * Original domaines table format - maintained for compatibility
     */
    public int createDomainesTable(Sheet sheet, Indicateur indicateur, int rowIdx,
            CellStyle headerStyle, CellStyle borderStyle) {
        // Crée un style de header avec fond gris clair
        CellStyle customHeaderStyle = sheet.getWorkbook().createCellStyle();
        customHeaderStyle.cloneStyleFrom(headerStyle);
        Font font = sheet.getWorkbook().createFont();
        font.setBold(true);
        customHeaderStyle.setFont(font);
        customHeaderStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        customHeaderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Row domHeader = sheet.createRow(rowIdx++);
        ExcelUtils.createCell(domHeader, 0, "Domaines", customHeaderStyle);
        ExcelUtils.createCell(domHeader, 1, "Sous-domaines", customHeaderStyle);

        java.util.Set<String> domaines = new java.util.LinkedHashSet<>();
        java.util.Set<String> sousDomaines = new java.util.LinkedHashSet<>();
        if (indicateur.getSousDomaines() != null) {
            for (var sd : indicateur.getSousDomaines()) {
                if (sd.getDomaine() != null && sd.getDomaine().getNom() != null) {
                    domaines.add(sd.getDomaine().getNom());
                }
                if (sd.getNom() != null) {
                    sousDomaines.add(sd.getNom());
                }
            }
        }
        int maxDomRows = Math.max(domaines.size(), sousDomaines.size());
        var domIt = domaines.iterator();
        var sousDomIt = sousDomaines.iterator();
        for (int i = 0; i < maxDomRows; i++) {
            Row row = sheet.createRow(rowIdx++);
            ExcelUtils.createCell(row, 0, domIt.hasNext() ? domIt.next() : "", borderStyle);
            ExcelUtils.createCell(row, 1, sousDomIt.hasNext() ? sousDomIt.next() : "", borderStyle);
        }
        return rowIdx;
    }

    /**
     * Creates a configuration table with configuration metadata
     */
    public int createConfigurationTable(Sheet sheet, Indicateur indicateur, int rowIdx,
            CellStyle headerStyle, CellStyle borderStyle) {

        // Create configuration table using the facade
        MetaDataTable configTable = metaDataFacade.createConfigurationMetaData(indicateur);

        return excelRenderer.renderMetaDataTable(sheet, configTable, rowIdx, headerStyle, borderStyle);
    }

}
