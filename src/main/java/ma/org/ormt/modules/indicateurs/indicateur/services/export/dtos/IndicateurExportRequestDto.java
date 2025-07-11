package ma.org.ormt.modules.indicateurs.indicateur.services.export.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour configurer les exports d'indicateurs
 * Permet de spécifier les colonnes à exporter et le type de groupement
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IndicateurExportRequestDto {

    /**
     * Liste des IDs des indicateurs à exporter
     * Si null ou vide, tous les indicateurs seront exportés
     */
    private List<Long> indicateurIds;

    /**
     * Liste des colonnes à inclure dans l'export
     * Si null ou vide, toutes les colonnes seront exportées
     */
    private List<String> columnsToExport;

    /**
     * Type de groupement des données
     * - NONE: Toutes les données dans une seule feuille
     * - BY_DOMAINE: Grouper par domaine
     * - BY_SOURCE: Grouper par source
     */
    @Builder.Default
    private GroupingType groupBy = GroupingType.NONE;

    /**
     * Nom du fichier (sans extension)
     */
    @Builder.Default
    private String fileName = "indicateurs-export";

    /**
     * Format de fichier pour l'export
     * - EXCEL: Format Excel (.xlsx)
     * - CSV: Format CSV (.csv)
     */
    @Builder.Default
    private ExportFormat format = ExportFormat.EXCEL;

    /**
     * Liste des sections à inclure dans l'export détaillé par sheet
     * Valeurs possibles: META, DOMAINES, DIMENSIONS, PIVOT_DATA,
     * FLAT_DATA
     * Si null ou vide, toutes les sections seront exportées
     */
    @Builder.Default
    private List<String> sectionsToExport = List.of(
            ExportSection.META.getKey(),
            ExportSection.DOMAINES.getKey(),
            ExportSection.DIMENSIONS.getKey(),
            ExportSection.PIVOT_DATA.getKey(),
            ExportSection.FLAT_DATA.getKey());

    /**
     * Type de données à inclure dans l'export détaillé
     * - PIVOT: Données au format pivot (tableau croisé)
     * - FLAT: Données au format plat (une ligne par combinaison)
     * - BOTH: Les deux formats
     * - NONE: Aucune donnée
     */
    @Builder.Default
    private DataTableType dataTableType = DataTableType.BOTH;

    public enum DataTableType {
        PIVOT("pivot"),
        FLAT("flat"),
        BOTH("both"),
        NONE("none");

        private final String value;

        DataTableType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static DataTableType fromValue(String value) {
            for (DataTableType type : values()) {
                if (type.value.equalsIgnoreCase(value)) {
                    return type;
                }
            }
            return BOTH;
        }
    }

    public enum ExportSection {
        META("META", "Informations générales"),
        DOMAINES("DOMAINES", "Domaines et sous-domaines"),
        DIMENSIONS("DIMENSIONS", "Dimensions de l'indicateur"),
        CONFIGURATION("CONFIGURATION", "Configuration de l'indicateur"),
        PIVOT_DATA("PIVOT_DATA", "Données au format pivot"),
        FLAT_DATA("FLAT_DATA", "Données au format plat");

        private final String key;
        private final String description;

        ExportSection(String key, String description) {
            this.key = key;
            this.description = description;
        }

        public String getKey() {
            return key;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum GroupingType {
        NONE("none"),
        BY_DOMAINE("domaine"),
        BY_SOURCE("source");

        private final String value;

        GroupingType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static GroupingType fromValue(String value) {
            for (GroupingType type : values()) {
                if (type.value.equalsIgnoreCase(value)) {
                    return type;
                }
            }
            return NONE;
        }
    }

    public enum ExportFormat {
        EXCEL("xlsx"),
        CSV("csv");

        private final String extension;

        ExportFormat(String extension) {
            this.extension = extension;
        }

        public String getExtension() {
            return extension;
        }

        public static ExportFormat fromValue(String value) {
            for (ExportFormat format : values()) {
                if (format.name().equalsIgnoreCase(value)) {
                    return format;
                }
            }
            return EXCEL;
        }
    }
}
