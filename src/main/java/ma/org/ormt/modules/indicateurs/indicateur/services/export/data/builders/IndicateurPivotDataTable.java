package ma.org.ormt.modules.indicateurs.indicateur.services.export.data.builders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import ma.org.ormt.modules.indicateurs.donnee.models.DonneeIndicateur;
import ma.org.ormt.modules.indicateurs.indicateur.association.dimension.models.IndicateurDimension;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;
import ma.org.ormt.modules.indicateurs.valeurdimension.models.ValeurDimension;

public class IndicateurPivotDataTable {

    public static List<List<String>> buildPivotTableData(Indicateur indicateur) {
        // Early validation to prevent division by zero and null pointer exceptions
        if (indicateur == null || indicateur.getIndicateurDimensions() == null ||
                indicateur.getIndicateurDimensions().isEmpty() ||
                indicateur.getDonnees() == null || indicateur.getDonnees().isEmpty()) {
            return Collections.emptyList();
        }

        List<IndicateurDimension> dims = indicateur.getIndicateurDimensions();
        IndicateurDimension principale = dims.stream().filter(IndicateurDimension::getPrincipale).findFirst()
                .orElse(null);
        List<IndicateurDimension> autres = dims.stream().filter(d -> !Boolean.TRUE.equals(d.getPrincipale()))
                .collect(Collectors.toList());
        if (principale == null || autres.isEmpty())
            return Collections.emptyList();

        // Find temporelle dimension if exists and move it to the top
        Optional<IndicateurDimension> temporelleOpt = autres.stream()
                .filter(d -> Boolean.TRUE.equals(d.getTemporelle())).findFirst();
        List<IndicateurDimension> columnDims = new ArrayList<>();
        if (temporelleOpt.isPresent()) {
            columnDims.add(temporelleOpt.get());
            for (IndicateurDimension d : autres) {
                if (!d.equals(temporelleOpt.get())) {
                    columnDims.add(d);
                }
            }
        } else {
            columnDims.addAll(autres);
        }

        List<List<String>> columnValues = columnDims.stream()
                .map(dim -> getUniqueDimensionValues(indicateur, dim.getDimension().getNom().toLowerCase()))
                .collect(Collectors.toList());
        List<List<String>> columnCombinations = cartesianProduct(columnValues);

        // Build header rows dynamically (no combination row at the end)
        List<List<String>> headerRows = new ArrayList<>();
        int numColDims = columnDims.size();
        int numCols = columnCombinations.size();
        for (int i = 0; i < numColDims; i++) {
            List<String> headerRow = new ArrayList<>();
            headerRow.add(
                    i == 0 ? (principale.getDimension().getLibelle() != null
                            ? principale.getDimension().getLibelle().toLowerCase()
                            : principale.getDimension().getNom().toLowerCase()) : "");
            int repeat = 1;
            for (int j = 0; j < i; j++) {
                repeat *= columnValues.get(j).size();
            }

            // Prevent division by zero
            int columnSize = columnValues.get(i).size();
            if (repeat == 0 || columnSize == 0 || numCols == 0) {
                continue; // Skip this iteration to avoid division by zero
            }

            int block = numCols / (repeat * columnSize);
            for (int r = 0; r < repeat; r++) {
                for (String val : columnValues.get(i)) {
                    for (int b = 0; b < block; b++) {
                        headerRow.add(val);
                    }
                }
            }
            headerRows.add(headerRow);
        }

        // Build data rows
        List<String> valeursPrincipale = getUniqueDimensionValues(indicateur, principale.getDimension().getNom());
        List<List<String>> data = new ArrayList<>();
        for (String valPrincipale : valeursPrincipale) {
            List<String> row = new ArrayList<>();
            row.add(valPrincipale);
            for (List<String> comb : columnCombinations) {
                Optional<DonneeIndicateur> donneeOpt = indicateur.getDonnees().stream().filter(d -> {
                    String vdPrincipale = getValeurDimension(d, principale.getDimension().getNom());
                    if (!Objects.equals(vdPrincipale, valPrincipale))
                        return false;
                    for (int idx = 0; idx < columnDims.size(); idx++) {
                        String vd = getValeurDimension(d, columnDims.get(idx).getDimension().getNom());
                        if (!Objects.equals(vd, comb.get(idx)))
                            return false;
                    }
                    return true;
                }).findFirst();
                row.add(donneeOpt.map(DonneeIndicateur::getValeur).orElse(""));
            }
            data.add(row);
        }
        List<List<String>> result = new ArrayList<>();
        for (List<String> header : headerRows) {
            result.add(header);
        }
        result.addAll(data);
        return result;
    }

    public static List<String> getUniqueDimensionValues(Indicateur indicateur, String dimensionNom) {
        Set<String> values = new LinkedHashSet<>();
        for (DonneeIndicateur donnee : indicateur.getDonnees()) {
            String val = getValeurDimension(donnee, dimensionNom);
            if (val != null)
                values.add(val);
        }
        return new ArrayList<>(values);
    }

    public static String getValeurDimension(DonneeIndicateur donnee, String dimensionNom) {
        if (donnee.getValeurDimensions() == null)
            return null;
        return donnee.getValeurDimensions().stream()
                .filter(vd -> dimensionNom.equals(vd.getDimension().getNom()))
                .map(ValeurDimension::getValeur)
                .findFirst().orElse("");
    }

    public static List<List<String>> cartesianProduct(List<List<String>> lists) {
        List<List<String>> result = new ArrayList<>();
        if (lists.isEmpty()) {
            result.add(new ArrayList<>());
            return result;
        } else {
            cartesianProductRecursive(lists, result, 0, new ArrayList<>());
            return result;
        }
    }

    private static void cartesianProductRecursive(List<List<String>> lists, List<List<String>> result, int depth,
            List<String> current) {
        if (depth == lists.size()) {
            result.add(new ArrayList<>(current));
            return;
        }
        for (String s : lists.get(depth)) {
            current.add(s);
            cartesianProductRecursive(lists, result, depth + 1, current);
            current.remove(current.size() - 1);
        }
    }
}
