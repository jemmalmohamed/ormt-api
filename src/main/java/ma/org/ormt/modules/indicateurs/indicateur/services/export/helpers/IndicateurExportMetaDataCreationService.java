package ma.org.ormt.modules.indicateurs.indicateur.services.export.helpers;

import java.util.List;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.stereotype.Service;

import ma.org.ormt.core.utilities.ExcelUtils;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;

@Service
public class IndicateurExportMetaDataCreationService {

    public int createDimensionsTable(Sheet sheet, Indicateur indicateur, int startRowIdx,
            org.apache.poi.ss.usermodel.CellStyle headerStyle, org.apache.poi.ss.usermodel.CellStyle borderStyle) {
        // Ajout d'un header global pour l'ensemble des dimensions
        Row globalHeader = sheet.createRow(startRowIdx++);
        // Crée un style de header avec fond gris clair
        org.apache.poi.ss.usermodel.CellStyle customHeaderStyle = sheet.getWorkbook().createCellStyle();
        customHeaderStyle.cloneStyleFrom(headerStyle);
        org.apache.poi.ss.usermodel.Font font = sheet.getWorkbook().createFont();
        font.setBold(true);
        customHeaderStyle.setFont(font);
        customHeaderStyle.setFillForegroundColor(org.apache.poi.ss.usermodel.IndexedColors.GREY_25_PERCENT.getIndex());
        customHeaderStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
        ExcelUtils.createCell(globalHeader, 0, "Dimensions", customHeaderStyle);
        int maxRows = 0;
        int colIdx = 0;
        if (indicateur.getIndicateurDimensions() != null) {
            for (var indDim : indicateur.getIndicateurDimensions()) {
                int rowIdx = startRowIdx;
                String nom = indDim.getDimension() != null ? indDim.getDimension().getNom() : "";
                String libelle = indDim.getDimension() != null && indDim.getDimension().getLibelle() != null
                        ? indDim.getDimension().getLibelle()
                        : "";
                String principale = indDim.getPrincipale() != null ? (indDim.getPrincipale() ? "Oui" : "Non") : "";
                String temporelle = indDim.getTemporelle() != null ? (indDim.getTemporelle() ? "Oui" : "Non") : "";
                String description = indDim.getDimension() != null && indDim.getDimension().getDescription() != null
                        ? indDim.getDimension().getDescription()
                        : "";
                java.util.List<String> valeurs = new java.util.ArrayList<>();
                if (indicateur.getDonnees() != null) {
                    for (var donnee : indicateur.getDonnees()) {
                        if (donnee.getValeurDimensions() != null) {
                            for (var vd : donnee.getValeurDimensions()) {
                                if (vd.getDimension() != null && nom.equals(vd.getDimension().getNom())) {
                                    if (vd.getValeur() != null) {
                                        valeurs.add(vd.getValeur());
                                    }
                                }
                            }
                        }
                    }
                }
                // Prépare les lignes du tableau vertical pour cette dimension
                java.util.List<String[]> dimRows = new java.util.ArrayList<>();
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

    public int createDataStatsTable(Sheet sheet, Indicateur indicateur, int rowIdx,
            org.apache.poi.ss.usermodel.CellStyle headerStyle, org.apache.poi.ss.usermodel.CellStyle borderStyle) {
        // Crée un style de header avec fond gris clair
        org.apache.poi.ss.usermodel.CellStyle customHeaderStyle = sheet.getWorkbook().createCellStyle();
        customHeaderStyle.cloneStyleFrom(headerStyle);
        org.apache.poi.ss.usermodel.Font font = sheet.getWorkbook().createFont();
        font.setBold(true);
        customHeaderStyle.setFont(font);
        customHeaderStyle.setFillForegroundColor(org.apache.poi.ss.usermodel.IndexedColors.GREY_25_PERCENT.getIndex());
        customHeaderStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
        Row dataHeader = sheet.createRow(rowIdx++);
        String[] dataHeaders = { "Nb Données", "A des données" };
        for (int i = 0; i < dataHeaders.length; i++) {
            ExcelUtils.createCell(dataHeader, i, dataHeaders[i], customHeaderStyle);
        }
        int nbDonnees = indicateur.getDonnees() != null ? indicateur.getDonnees().size() : 0;
        String aDonnees = nbDonnees > 0 ? "Oui" : "Non";
        Row dataRow = sheet.createRow(rowIdx++);
        String[] dataVals = { String.valueOf(nbDonnees), aDonnees };
        for (int i = 0; i < dataVals.length; i++) {
            ExcelUtils.createCell(dataRow, i, dataVals[i], borderStyle);
        }
        return rowIdx;
    }

    public int createDomainesTable(Sheet sheet, Indicateur indicateur, int rowIdx,
            org.apache.poi.ss.usermodel.CellStyle headerStyle, org.apache.poi.ss.usermodel.CellStyle borderStyle) {
        // Crée un style de header avec fond gris clair
        org.apache.poi.ss.usermodel.CellStyle customHeaderStyle = sheet.getWorkbook().createCellStyle();
        customHeaderStyle.cloneStyleFrom(headerStyle);
        org.apache.poi.ss.usermodel.Font font = sheet.getWorkbook().createFont();
        font.setBold(true);
        customHeaderStyle.setFont(font);
        customHeaderStyle.setFillForegroundColor(org.apache.poi.ss.usermodel.IndexedColors.GREY_25_PERCENT.getIndex());
        customHeaderStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
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

    public int createMetaTable(Sheet sheet, Indicateur indicateur, int rowIdx,
            CellStyle headerStyle, CellStyle borderStyle) {
        // Crée un style de header avec fond gris clair
        CellStyle customHeaderStyle = sheet.getWorkbook().createCellStyle();
        customHeaderStyle.cloneStyleFrom(headerStyle);
        Font font = sheet.getWorkbook().createFont();
        font.setBold(true);
        customHeaderStyle.setFont(font);
        customHeaderStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        customHeaderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        // Use the new helper to get the metadata rows
        List<List<String>> metaRows = IndicateurMetaDataTable.buildMetaTableData(indicateur);
        for (List<String> metaRow : metaRows) {
            Row row = sheet.createRow(rowIdx++);
            ExcelUtils.createCell(row, 0, metaRow.get(0), customHeaderStyle);
            ExcelUtils.createCell(row, 1, metaRow.get(1), borderStyle);
        }
        return rowIdx;
    }

}
