package ma.org.ormt.modules.indicateurs.indicateur.services.export.meta.builders;

import java.util.List;

import org.springframework.stereotype.Component;

import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.meta.models.MetaDataRow;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.meta.models.MetaDataSection;

/**
 * Builder for creating structured metadata tables from Indicateur data
 */
@Component
public class MetaDataTableBuilder {

        /**
         * Builds configuration section
         */
        public MetaDataSection buildConfigurationSection(Indicateur indicateur) {
                MetaDataSection section = new MetaDataSection("Configuration");

                section.addRow(new MetaDataRow("Type de tableau de bord",
                                indicateur != null && indicateur.getTypeTb() != null ? indicateur.getTypeTb() : ""));

                // section.addRow(new MetaDataRow("Type de graphique",
                // indicateur != null && indicateur.getTypeGraphe() != null ?
                // indicateur.getTypeGraphe()
                // : ""));

                return section;
        }

        /**
         * Builds data statistics section
         */
        public MetaDataSection buildDataStatsSection(Indicateur indicateur) {
                MetaDataSection section = new MetaDataSection("Statistiques des données");

                int nbDonnees = (indicateur != null && indicateur.getDonnees() != null) ? indicateur.getDonnees().size()
                                : 0;

                section.addRow(new MetaDataRow("Nombre de données", String.valueOf(nbDonnees)));
                section.addRow(new MetaDataRow("Contient des données", nbDonnees > 0 ? "Oui" : "Non"));

                // Additional data statistics
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

        /**
         * Builds selective information section with only specified fields
         * 
         * @param indicateur      The indicator
         * @param fieldsToInclude List of field names to include (e.g., "ID", "NOM",
         *                        "DESCRIPTION", "SOURCE", etc.)
         * @return MetaDataSection with only the requested fields
         */
        public MetaDataSection buildInformationSection(Indicateur indicateur, List<String> fieldsToInclude) {
                MetaDataSection section = new MetaDataSection("Informations de base");

                // If no specific fields requested, include all fields by default
                if (fieldsToInclude == null || fieldsToInclude.isEmpty()) {
                        fieldsToInclude = List.of("ID", "NOM", "ABREVIATION", "SOURCE", "ACTIF",
                                        "DESCRIPTION", "REGLE_CALCUL", "UNITE", "CATEGORIE");
                }

                // Add only requested fields
                for (String field : fieldsToInclude) {
                        switch (field.toUpperCase()) {
                                case "ID":
                                        section.addRow(new MetaDataRow("ID",
                                                        indicateur != null && indicateur.getId() != null
                                                                        ? String.valueOf(indicateur.getId())
                                                                        : ""));
                                        break;
                                case "NOM":
                                case "NAME":
                                        section.addRow(new MetaDataRow("Indicateur",
                                                        indicateur != null && indicateur.getNom() != null
                                                                        ? indicateur.getNom()
                                                                        : ""));
                                        break;
                                case "ABREVIATION":
                                case "ABBREVIATION":
                                        section.addRow(new MetaDataRow("Abréviation",
                                                        indicateur != null && indicateur.getAbreviation() != null
                                                                        ? indicateur.getAbreviation()
                                                                        : ""));
                                        break;
                                case "SOURCE":
                                        section.addRow(new MetaDataRow("Source",
                                                        (indicateur != null && indicateur.getSource() != null &&
                                                                        indicateur.getSource().getNom() != null)
                                                                                        ? indicateur.getSource()
                                                                                                        .getNom()
                                                                                        : ""));
                                        break;
                                case "ACTIF":
                                case "ACTIVE":
                                        section.addRow(new MetaDataRow("Actif",
                                                        (indicateur != null && indicateur.getActif() != null)
                                                                        ? (indicateur.getActif() ? "Oui" : "Non")
                                                                        : "Non défini"));
                                        break;
                                case "DESCRIPTION":
                                        section.addRow(new MetaDataRow("Description",
                                                        indicateur != null && indicateur.getDescription() != null
                                                                        ? indicateur.getDescription()
                                                                        : ""));
                                        break;
                                case "REGLE_CALCUL":
                                case "REGLE":
                                        section.addRow(new MetaDataRow("Règle de calcul",
                                                        indicateur != null && indicateur.getRegleCalcul() != null
                                                                        ? indicateur.getRegleCalcul()
                                                                        : ""));
                                        break;
                                case "UNITE":
                                case "UNIT":
                                        section.addRow(new MetaDataRow("Unité",
                                                        indicateur != null && indicateur.getUnite() != null
                                                                        ? indicateur.getUnite()
                                                                        : ""));
                                        break;
                                case "CATEGORIE":
                                case "CATEGORY":
                                        section.addRow(new MetaDataRow("Catégorie",
                                                        indicateur != null && indicateur.getCategorie() != null
                                                                        ? indicateur.getCategorie()
                                                                        : ""));
                                        break;
                                default:
                                        // Unknown field, skip
                                        break;
                        }
                }

                return section;
        }
}
