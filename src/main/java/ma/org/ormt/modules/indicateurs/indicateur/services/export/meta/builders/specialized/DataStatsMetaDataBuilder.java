package ma.org.ormt.modules.indicateurs.indicateur.services.export.meta.builders.specialized;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.meta.models.MetaDataRow;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.meta.models.MetaDataSection;

/**
 * Specialized builder for data statistics metadata
 */
@Component
public class DataStatsMetaDataBuilder {

    /**
     * Builds comprehensive data statistics section
     */
    public MetaDataSection buildDetailedDataStatsSection(Indicateur indicateur) {
        MetaDataSection section = new MetaDataSection("Statistiques détaillées des données");

        if (indicateur == null || indicateur.getDonnees() == null) {
            section.addRow(new MetaDataRow("État", "Aucune donnée disponible"));
            return section;
        }

        int totalData = indicateur.getDonnees().size();
        section.addRow(new MetaDataRow("Nombre total de données", String.valueOf(totalData)));

        if (totalData == 0) {
            section.addRow(new MetaDataRow("État", "Pas de données disponibles"));
            return section;
        }

        // Data with values
        long dataWithValues = indicateur.getDonnees().stream()
                .filter(d -> d.getValeur() != null)
                .count();

        section.addRow(new MetaDataRow("Données avec valeur", String.valueOf(dataWithValues)));
        section.addRow(new MetaDataRow("Données sans valeur", String.valueOf(totalData - dataWithValues)));

        double completionRate = (double) dataWithValues / totalData * 100;
        section.addRow(new MetaDataRow("Taux de complétude", String.format("%.1f%%", completionRate)));

        // Analyze numerical values if available
        List<Double> numericValues = indicateur.getDonnees().stream()
                .filter(d -> d.getValeur() != null)
                .map(d -> {
                    try {
                        return Double.parseDouble(d.getValeur().toString());
                    } catch (NumberFormatException e) {
                        return null;
                    }
                })
                .filter(v -> v != null)
                .collect(Collectors.toList());

        if (!numericValues.isEmpty()) {
            section.addRow(new MetaDataRow("Données numériques", String.valueOf(numericValues.size())));

            double min = numericValues.stream().mapToDouble(Double::doubleValue).min().orElse(0);
            double max = numericValues.stream().mapToDouble(Double::doubleValue).max().orElse(0);
            double avg = numericValues.stream().mapToDouble(Double::doubleValue).average().orElse(0);

            section.addRow(new MetaDataRow("Valeur minimale", formatNumber(min)));
            section.addRow(new MetaDataRow("Valeur maximale", formatNumber(max)));
            section.addRow(new MetaDataRow("Valeur moyenne", formatNumber(avg)));
        }

        // Data with dimensions
        if (indicateur.getIndicateurDimensions() != null && !indicateur.getIndicateurDimensions().isEmpty()) {
            long dataWithDimensions = indicateur.getDonnees().stream()
                    .filter(d -> d.getValeurDimensions() != null && !d.getValeurDimensions().isEmpty())
                    .count();

            section.addRow(new MetaDataRow("Données avec dimensions", String.valueOf(dataWithDimensions)));

            if (dataWithDimensions > 0) {
                double dimensionCompletionRate = (double) dataWithDimensions / totalData * 100;
                section.addRow(new MetaDataRow("Taux de complétude des dimensions",
                        String.format("%.1f%%", dimensionCompletionRate)));
            }
        }

        // Date range analysis if temporal dimensions exist
        boolean hasTemporalDimension = indicateur.getIndicateurDimensions() != null &&
                indicateur.getIndicateurDimensions().stream()
                        .anyMatch(dim -> Boolean.TRUE.equals(dim.getTemporelle()));

        if (hasTemporalDimension) {
            section.addRow(new MetaDataRow("Contient des données temporelles", "Oui"));
            // Additional temporal analysis could be added here
        } else {
            section.addRow(new MetaDataRow("Contient des données temporelles", "Non"));
        }

        return section;
    }

    /**
     * Builds a simple data statistics section
     */
    public MetaDataSection buildSimpleDataStatsSection(Indicateur indicateur) {
        MetaDataSection section = new MetaDataSection("Statistiques des données");

        int nbDonnees = (indicateur != null && indicateur.getDonnees() != null) ? indicateur.getDonnees().size() : 0;

        section.addRow(new MetaDataRow("Nombre de données", String.valueOf(nbDonnees)));
        section.addRow(new MetaDataRow("Contient des données", nbDonnees > 0 ? "Oui" : "Non"));

        if (nbDonnees > 0 && indicateur != null && indicateur.getDonnees() != null) {
            long dataWithValues = indicateur.getDonnees().stream()
                    .filter(d -> d.getValeur() != null)
                    .count();

            section.addRow(new MetaDataRow("Données avec valeurs", String.valueOf(dataWithValues)));

            if (dataWithValues > 0) {
                double completionRate = (double) dataWithValues / nbDonnees * 100;
                section.addRow(new MetaDataRow("Taux de complétude",
                        String.format("%.1f%%", completionRate)));
            }
        }

        return section;
    }

    private String formatNumber(double number) {
        if (number == Math.floor(number)) {
            return String.valueOf((long) number);
        } else {
            return String.format("%.2f", number);
        }
    }
}
