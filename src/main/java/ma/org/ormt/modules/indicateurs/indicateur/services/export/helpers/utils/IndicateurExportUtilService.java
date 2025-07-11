package ma.org.ormt.modules.indicateurs.indicateur.services.export.helpers.utils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import ma.org.ormt.modules.indicateurs.indicateur.services.export.dtos.IndicateurExportRequestDto;
import lombok.extern.slf4j.Slf4j;

/**
 * Service utilitaire pour les exports d'indicateurs
 */
@Service
@Slf4j
public class IndicateurExportUtilService {

    // Enum pour centraliser la gestion des colonnes et des titres
    public enum IndicateurColumn {
        ESPACES("Espaces"),
        DOMAINES("Domaines"),
        SOUS_DOMAINES("Sous domaines"),
        ID("id"),
        NOM("Nom"),
        UNITE("Unité"),
        CATEGORIE("Catégorie"),
        SOURCE("Source"),
        ABREVIATION("Abréviation"),
        TYPE_TB("Type TB"),
        TYPE_GRAPHE("Type Graphe"),
        DESCRIPTION("Description"),
        REGLE_CALCUL("Règle de calcul"),
        ACTIF("Actif"),
        HAS_DATA("A des données");

        private final String header;

        IndicateurColumn(String header) {
            this.header = header;
        }

        public String getHeader() {
            return header;
        }
    }

    /**
     * Détermine les colonnes à exporter selon la configuration
     */
    public List<IndicateurColumn> getColumnsToExport(List<String> requestedColumns) {
        if (requestedColumns == null || requestedColumns.isEmpty()) {
            return Arrays.asList(IndicateurColumn.values());
        }

        return requestedColumns.stream()
                .map(this::findColumnByName)
                .filter(col -> col != null)
                .collect(Collectors.toList());
    }

    /**
     * Trouve une colonne par son nom
     */
    private IndicateurColumn findColumnByName(String columnName) {
        if (!StringUtils.hasText(columnName)) {
            return null;
        }

        try {
            return IndicateurColumn.valueOf(columnName.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Colonne inconnue demandée: {}", columnName);
            return null;
        }
    }

    /**
     * Détermine les sections à exporter pour l'export détaillé
     */
    public List<String> getSectionsToExport(List<String> requestedSections) {
        if (requestedSections == null || requestedSections.isEmpty()) {
            // Par défaut, toutes les sections sauf les tables de données
            return Arrays.asList("META", "DOMAINES", "DIMENSIONS");
        }

        return requestedSections.stream()
                .filter(this::isValidSection)
                .collect(Collectors.toList());
    }

    /**
     * Valide qu'une section est reconnue
     */
    public boolean isValidSection(String section) {
        if (!StringUtils.hasText(section)) {
            return false;
        }

        try {
            IndicateurExportRequestDto.ExportSection.valueOf(section.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            log.warn("Section inconnue demandée: {}", section);
            return false;
        }
    }

    /**
     * Nettoie le nom d'une feuille Excel
     */
    public String cleanSheetName(String name) {
        if (!StringUtils.hasText(name)) {
            return "Feuille";
        }

        // Remplacer les caractères interdits dans les noms de feuilles Excel
        String cleaned = name.replaceAll("[\\[\\]\\*\\?:/\\\\]", "_");

        // Limiter à 31 caractères (limite Excel)
        if (cleaned.length() > 31) {
            cleaned = cleaned.substring(0, 31);
        }

        return cleaned;
    }

    /**
     * Assure l'unicité du nom de feuille
     */
    public String ensureUniqueSheetName(String baseName, java.util.Set<String> usedNames) {
        String sheetName = baseName;
        int suffix = 1;

        while (usedNames.contains(sheetName)) {
            String suffixStr = "_" + suffix;
            int maxLen = 31 - suffixStr.length();
            sheetName = (baseName.length() > maxLen ? baseName.substring(0, maxLen) : baseName) + suffixStr;
            suffix++;
        }

        return sheetName;
    }

    /**
     * Détermine si une table de données doit être incluse
     */
    public boolean shouldIncludeDataTable(IndicateurExportRequestDto.DataTableType dataTableType, String tableType) {
        if (dataTableType == null) {
            return true; // Par défaut, inclure toutes les tables
        }

        switch (dataTableType) {
            case BOTH:
                return true;
            case PIVOT:
                return "PIVOT".equals(tableType);
            case FLAT:
                return "FLAT".equals(tableType);
            case NONE:
                return false;
            default:
                return true;
        }
    }
}
