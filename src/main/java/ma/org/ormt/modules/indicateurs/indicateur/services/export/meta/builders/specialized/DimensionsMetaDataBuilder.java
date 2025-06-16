package ma.org.ormt.modules.indicateurs.indicateur.services.export.meta.builders.specialized;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.meta.models.MetaDataRow;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.meta.models.MetaDataSection;

/**
 * Specialized builder for dimensions metadata
 */
@Component
public class DimensionsMetaDataBuilder {

        /**
         * Builds detailed dimensions section with comprehensive information
         */
        public MetaDataSection buildDetailedDimensionsSection(Indicateur indicateur) {
                MetaDataSection section = new MetaDataSection("Dimensions détaillées");

                if (indicateur == null || indicateur.getIndicateurDimensions() == null ||
                                indicateur.getIndicateurDimensions().isEmpty()) {
                        section.addRow(new MetaDataRow("État", "Aucune dimension définie"));
                        return section;
                }

                // Summary information
                section.addRow(new MetaDataRow("Nombre total de dimensions",
                                String.valueOf(indicateur.getIndicateurDimensions().size())));

                long principaleDimensions = indicateur.getIndicateurDimensions().stream()
                                .filter(ind -> Boolean.TRUE.equals(ind.getPrincipale()))
                                .count();

                long temporelleDimensions = indicateur.getIndicateurDimensions().stream()
                                .filter(ind -> Boolean.TRUE.equals(ind.getTemporelle()))
                                .count();

                section.addRow(new MetaDataRow("Dimensions principales", String.valueOf(principaleDimensions)));
                section.addRow(new MetaDataRow("Dimensions temporelles", String.valueOf(temporelleDimensions)));

                // Detailed dimension information
                for (int i = 0; i < indicateur.getIndicateurDimensions().size(); i++) {
                        var indDim = indicateur.getIndicateurDimensions().get(i);
                        String prefix = "Dimension " + (i + 1);

                        if (indDim.getDimension() != null) {
                                section.addRow(new MetaDataRow(prefix + " - Nom",
                                                indDim.getDimension().getNom() != null ? indDim.getDimension().getNom()
                                                                : ""));

                                section.addRow(new MetaDataRow(prefix + " - Libellé",
                                                indDim.getDimension().getLibelle() != null
                                                                ? indDim.getDimension().getLibelle()
                                                                : ""));

                                section.addRow(new MetaDataRow(prefix + " - Description",
                                                indDim.getDimension().getDescription() != null
                                                                ? indDim.getDimension().getDescription()
                                                                : ""));

                                section.addRow(new MetaDataRow(prefix + " - Principale",
                                                Boolean.TRUE.equals(indDim.getPrincipale()) ? "Oui" : "Non"));

                                section.addRow(new MetaDataRow(prefix + " - Temporelle",
                                                Boolean.TRUE.equals(indDim.getTemporelle()) ? "Oui" : "Non"));

                                // Get possible values for this dimension
                                Set<String> valeurs = new LinkedHashSet<>();
                                if (indicateur.getDonnees() != null) {
                                        indicateur.getDonnees().stream()
                                                        .filter(donnee -> donnee.getValeurDimensions() != null)
                                                        .flatMap(donnee -> donnee.getValeurDimensions().stream())
                                                        .filter(vd -> vd.getDimension() != null &&
                                                                        indDim.getDimension().getNom().equals(
                                                                                        vd.getDimension().getNom()))
                                                        .filter(vd -> vd.getValeur() != null)
                                                        .forEach(vd -> valeurs.add(vd.getValeur()));
                                }

                                section.addRow(new MetaDataRow(prefix + " - Nombre de valeurs possibles",
                                                String.valueOf(valeurs.size())));

                                if (!valeurs.isEmpty()) {
                                        String valeursStr = valeurs.size() <= 10 ? String.join(", ", valeurs)
                                                        : String.join(", ",
                                                                        valeurs.stream().limit(10)
                                                                                        .collect(Collectors.toList()))
                                                                        +
                                                                        " (et " + (valeurs.size() - 10) + " autres...)";

                                        section.addRow(new MetaDataRow(prefix + " - Valeurs", valeursStr));
                                }
                        }
                }

                return section;
        }

}
