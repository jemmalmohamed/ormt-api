package ma.org.ormt.modules.indicateurs.indicateur.services.export.meta.builders.specialized;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import ma.org.ormt.modules.indicateurs.indicateur.association.dimension.models.IndicateurDimension;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.meta.models.MetaDataRow;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.meta.models.MetaDataSection;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.meta.models.MetaDataTable;

/**
 * Specialized builder for creating horizontal dimensions layout metadata
 * This preserves the original horizontal columns approach where each dimension
 * gets its own pair of columns side by side
 */
@Component
public class HorizontalDimensionsMetaDataBuilder {

    /**
     * Builds dimensions table with horizontal layout (columns for each dimension)
     * This matches the original createDimensionsTable format
     * 
     * @param indicateur The indicator containing dimensions
     * @return MetaDataTable with horizontal dimensions layout
     */
    public MetaDataTable buildHorizontalDimensionsTable(Indicateur indicateur) {
        MetaDataTable table = new MetaDataTable();

        if (indicateur == null || indicateur.getIndicateurDimensions() == null ||
                indicateur.getIndicateurDimensions().isEmpty()) {
            // Create simple table with no dimensions message
            MetaDataSection section = new MetaDataSection("Dimensions");
            section.addRow(new MetaDataRow("Message", "Aucune dimension définie"));
            table.addSection(section);
            return table;
        }

        // Create the horizontal structure
        List<DimensionColumn> dimensionColumns = extractDimensionColumns(indicateur);
        MetaDataSection dimensionsSection = buildHorizontalDimensionsSection(dimensionColumns);
        table.addSection(dimensionsSection);

        return table;
    }

    /**
     * Extracts dimension data into column structures
     */
    private List<DimensionColumn> extractDimensionColumns(Indicateur indicateur) {
        List<DimensionColumn> columns = new ArrayList<>();

        for (var indDim : indicateur.getIndicateurDimensions()) {
            if (indDim.getDimension() != null) {
                DimensionColumn column = new DimensionColumn();

                // Extract basic info
                column.nom = indDim.getDimension().getNom() != null ? indDim.getDimension().getNom() : "";

                column.libelle = indDim.getDimension().getLibelle() != null
                        ? indDim.getDimension().getLibelle().toLowerCase()
                        : "";

                column.principale = indDim.getPrincipale() != null ? (indDim.getPrincipale() ? "Oui" : "Non") : "";

                column.temporelle = indDim.getTemporelle() != null ? (indDim.getTemporelle() ? "Oui" : "Non") : "";

                column.description = indDim.getDimension().getDescription() != null
                        ? indDim.getDimension().getDescription().toLowerCase()
                        : "";

                // Extract values
                column.valeurs = extractDimensionValues(indDim, indicateur);

                columns.add(column);
            }
        }

        return columns;
    }

    /**
     * Builds the horizontal dimensions section with proper column structure
     */
    private MetaDataSection buildHorizontalDimensionsSection(List<DimensionColumn> dimensionColumns) {
        MetaDataSection section = new MetaDataSection("Dimensions");

        // Create the header with dimension names
        List<String> headerRow = new ArrayList<>();
        for (DimensionColumn column : dimensionColumns) {
            headerRow.add(column.nom); // Column header
            headerRow.add(""); // Empty for the value column
        }
        if (!headerRow.isEmpty()) {
            section.addRow(new MetaDataRow("Header", String.join("|", headerRow)));
        }

        // Create rows for each property
        addPropertyRow(section, dimensionColumns, "Nom", col -> col.nom);
        addPropertyRow(section, dimensionColumns, "Libelle", col -> col.libelle);
        addPropertyRow(section, dimensionColumns, "Principale", col -> col.principale);
        addPropertyRow(section, dimensionColumns, "Temporelle", col -> col.temporelle);
        addPropertyRow(section, dimensionColumns, "Description", col -> col.description);

        // Add values rows
        addValuesRows(section, dimensionColumns);

        return section;
    }

    /**
     * Adds a property row for all dimensions
     */
    private void addPropertyRow(MetaDataSection section, List<DimensionColumn> columns,
            String propertyName, DimensionPropertyExtractor extractor) {
        List<String> rowData = new ArrayList<>();

        for (DimensionColumn column : columns) {
            rowData.add(propertyName);
            rowData.add(extractor.extract(column));
        }

        section.addRow(new MetaDataRow(propertyName + "_Row", String.join("|", rowData)));
    }

    /**
     * Adds values rows for all dimensions
     */
    private void addValuesRows(MetaDataSection section, List<DimensionColumn> columns) {
        // Find the maximum number of values across all dimensions
        int maxValues = columns.stream()
                .mapToInt(col -> col.valeurs.size())
                .max()
                .orElse(0);

        if (maxValues > 0) {
            // Add "Valeurs" header row
            List<String> valuesHeaderRow = new ArrayList<>();
            for (int i = 0; i < columns.size(); i++) {
                valuesHeaderRow.add("Valeurs");
                valuesHeaderRow.add(""); // Merged cell placeholder
            }
            section.addRow(new MetaDataRow("Valeurs_Header", String.join("|", valuesHeaderRow)));

            // Add value rows
            for (int i = 0; i < maxValues; i++) {
                List<String> valueRow = new ArrayList<>();
                for (DimensionColumn column : columns) {
                    valueRow.add(""); // Empty for label column
                    valueRow.add(i < column.valeurs.size() ? column.valeurs.get(i) : "");
                }
                section.addRow(new MetaDataRow("Value_" + i, String.join("|", valueRow)));
            }
        }
    }

    /**
     * Extracts all possible values for a dimension from the indicator's data
     */
    private List<String> extractDimensionValues(IndicateurDimension indDim, Indicateur indicateur) {
        Set<String> valeursSet = new LinkedHashSet<>();

        if (indicateur.getDonnees() != null && indDim.getDimension() != null) {
            String dimensionName = indDim.getDimension().getNom();

            for (var donnee : indicateur.getDonnees()) {
                if (donnee.getValeurDimensions() != null) {
                    for (var vd : donnee.getValeurDimensions()) {
                        if (vd.getDimension() != null &&
                                dimensionName.equals(vd.getDimension().getNom()) &&
                                vd.getValeur() != null) {
                            valeursSet.add(vd.getValeur());
                        }
                    }
                }
            }
        }

        return new ArrayList<>(valeursSet);
    }

    /**
     * Data structure to hold dimension column information
     */
    private static class DimensionColumn {
        String nom = "";
        String libelle = "";
        String principale = "";
        String temporelle = "";
        String description = "";
        List<String> valeurs = new ArrayList<>();
    }

    /**
     * Functional interface for extracting dimension properties
     */
    @FunctionalInterface
    private interface DimensionPropertyExtractor {
        String extract(DimensionColumn column);
    }
}
