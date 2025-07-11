package ma.org.ormt.modules.indicateurs.indicateur.services.export.data.builders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import ma.org.ormt.modules.indicateurs.donnee.models.DonneeIndicateur;
import ma.org.ormt.modules.indicateurs.indicateur.association.dimension.models.IndicateurDimension;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;

public class IndicateurCrudDataTable {

    /**
     * Build CRUD-friendly table data with IDs for operations
     * Format: [id, dimension1, dimension2, ..., valeur]
     */
    public static List<List<String>> buildCrudTableData(Indicateur indicateur) {
        List<IndicateurDimension> dims = indicateur.getIndicateurDimensions();
        if (dims == null || dims.isEmpty())
            return Collections.emptyList();

        // Header row: "ID" + dimension names + "Valeur"
        List<String> header = new ArrayList<>();
        header.add("id");
        header.addAll(dims.stream()
                .map(dim -> dim.getDimension().getLibelle().toLowerCase() != null
                        ? dim.getDimension().getLibelle().toLowerCase()
                        : dim.getDimension().getNom().toLowerCase())
                .collect(Collectors.toList()));
        header.add(indicateur.getNom() != null ? indicateur.getNom().toLowerCase() : "valeur");

        List<List<String>> result = new ArrayList<>();
        result.add(header);

        // Data rows: include actual data only (no empty combinations)
        for (DonneeIndicateur donnee : indicateur.getDonnees()) {
            List<String> row = new ArrayList<>();
            row.add(donnee.getId().toString()); // Add ID for CRUD operations

            // Add dimension values in order
            for (IndicateurDimension dim : dims) {
                String vd = IndicateurPivotDataTable.getValeurDimension(donnee, dim.getDimension().getNom());
                row.add(vd != null ? vd : "");
            }
            row.add(donnee.getValeur());
            result.add(row);
        }

        return result;
    }

    /**
     * Build template data for create operations (all possible combinations without
     * values)
     */
    public static List<List<String>> buildCreateTemplateData(Indicateur indicateur) {
        List<IndicateurDimension> dims = indicateur.getIndicateurDimensions();
        if (dims == null || dims.isEmpty())
            return Collections.emptyList();

        // Get all unique values for each dimension
        List<List<String>> allDimValues = dims.stream()
                .map(dim -> IndicateurPivotDataTable.getUniqueDimensionValues(indicateur, dim.getDimension().getNom()))
                .collect(Collectors.toList());

        // Cartesian product of all dimension values
        List<List<String>> allCombinations = IndicateurPivotDataTable.cartesianProduct(allDimValues);

        // Header row: dimension names + "Valeur"
        List<String> header = dims.stream()
                .map(dim -> dim.getDimension().getLibelle().toLowerCase() != null
                        ? dim.getDimension().getLibelle().toLowerCase()
                        : dim.getDimension().getNom().toLowerCase())
                .collect(Collectors.toList());
        header.add(indicateur.getNom() != null ? indicateur.getNom().toLowerCase() : "valeur");

        List<List<String>> result = new ArrayList<>();
        result.add(header);

        // For each combination, check if data exists
        for (List<String> comb : allCombinations) {
            Optional<DonneeIndicateur> donneeOpt = indicateur.getDonnees().stream().filter(d -> {
                for (int i = 0; i < dims.size(); i++) {
                    String vd = IndicateurPivotDataTable.getValeurDimension(d, dims.get(i).getDimension().getNom());
                    if (!Objects.equals(vd, comb.get(i)))
                        return false;
                }
                return true;
            }).findFirst();

            // Only add combinations that don't exist yet
            if (donneeOpt.isEmpty()) {
                List<String> row = new ArrayList<>(comb);
                row.add(""); // Empty value for user input
                result.add(row);
            }
        }
        return result;
    }
}
