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

public class IndicateurFlatDataTable {

    public static List<List<String>> buildFlatTableData(Indicateur indicateur) {
        // Early validation to prevent null pointer exceptions
        if (indicateur == null || indicateur.getIndicateurDimensions() == null ||
                indicateur.getIndicateurDimensions().isEmpty() ||
                indicateur.getDonnees() == null || indicateur.getDonnees().isEmpty()) {
            return Collections.emptyList();
        }

        List<IndicateurDimension> dims = indicateur.getIndicateurDimensions();
        if (dims == null || dims.isEmpty())
            return Collections.emptyList();

        // Get all unique values for each dimension
        List<List<String>> allDimValues = dims.stream()
                .map(dim -> IndicateurPivotDataTable.getUniqueDimensionValues(indicateur, dim.getDimension().getNom()))
                .collect(Collectors.toList());

        // Choroplethsian product of all dimension values
        List<List<String>> allCombinations = IndicateurPivotDataTable.choroplethsianProduct(allDimValues);

        // Header row: dimension names + "Valeur"
        List<String> header = dims.stream()
                .map(dim -> dim.getDimension().getLibelle() != null ? dim.getDimension().getLibelle().toLowerCase()
                        : dim.getDimension().getNom().toLowerCase())
                .collect(Collectors.toList());
        header.add(indicateur.getNom() != null ? indicateur.getNom().toLowerCase() : "valeur");

        List<List<String>> result = new ArrayList<>();
        result.add(header);

        // For each combination, find the value
        for (List<String> comb : allCombinations) {
            Optional<DonneeIndicateur> donneeOpt = indicateur.getDonnees().stream().filter(d -> {
                for (int i = 0; i < dims.size(); i++) {
                    String vd = IndicateurPivotDataTable.getValeurDimension(d, dims.get(i).getDimension().getNom());
                    if (!Objects.equals(vd, comb.get(i)))
                        return false;
                }
                return true;
            }).findFirst();
            List<String> row = new ArrayList<>(comb);
            row.add(donneeOpt.map(DonneeIndicateur::getValeur).orElse(""));
            result.add(row);
        }
        return result;
    }
}
