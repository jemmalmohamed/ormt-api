package ma.org.ormt.modules.indicateurs.indicateur.services.export.data.builders;

import java.util.List;

import org.springframework.stereotype.Service;

import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;
import lombok.extern.slf4j.Slf4j;

/**
 * Service centralisé pour la création de tables de données
 * Réunit tous les builders de données en un seul point d'accès
 */
@Service
@Slf4j
public class DataTableBuilderService {

    /**
     * Type de table de données
     */
    public enum DataTableType {
        PIVOT,
        FLAT,
        CRUD,
        CREATE_TEMPLATE
    }

    /**
     * Construit une table de données selon le type demandé
     */
    public List<List<String>> buildDataTable(Indicateur indicateur, DataTableType type) {
        if (indicateur == null) {
            log.warn("Indicateur null fourni pour la construction de table de données");
            return List.of();
        }

        try {
            switch (type) {
                case PIVOT:
                    return IndicateurPivotDataTable.buildPivotTableData(indicateur);
                case FLAT:
                    return IndicateurFlatDataTable.buildFlatTableData(indicateur);
                case CRUD:
                    return IndicateurCrudDataTable.buildCrudTableData(indicateur);
                case CREATE_TEMPLATE:
                    return IndicateurCrudDataTable.buildCreateTemplateData(indicateur);
                default:
                    log.warn("Type de table de données non supporté: {}", type);
                    return List.of();
            }
        } catch (Exception e) {
            log.error("Erreur lors de la construction de la table de données {} pour l'indicateur {}: {}",
                    type, indicateur.getId(), e.getMessage(), e);
            return List.of();
        }
    }

    /**
     * Vérifie si un indicateur a des données pour construire une table
     */
    public boolean hasValidDataForTable(Indicateur indicateur) {
        return indicateur != null &&
                indicateur.getIndicateurDimensions() != null &&
                !indicateur.getIndicateurDimensions().isEmpty() &&
                indicateur.getDonnees() != null &&
                !indicateur.getDonnees().isEmpty();
    }

    /**
     * Compte le nombre de lignes de données disponibles
     */
    public int getDataRowCount(Indicateur indicateur) {
        if (!hasValidDataForTable(indicateur)) {
            return 0;
        }
        return indicateur.getDonnees().size();
    }

    /**
     * Obtient les dimensions disponibles pour un indicateur
     */
    public List<String> getAvailableDimensions(Indicateur indicateur) {
        if (indicateur == null || indicateur.getIndicateurDimensions() == null) {
            return List.of();
        }

        return indicateur.getIndicateurDimensions().stream()
                .filter(dim -> dim.getDimension() != null)
                .map(dim -> dim.getDimension().getNom())
                .toList();
    }

    /**
     * Valide qu'un indicateur peut produire une table pivot
     */
    public boolean canBuildPivotTable(Indicateur indicateur) {
        if (!hasValidDataForTable(indicateur)) {
            return false;
        }

        // Besoin d'au moins une dimension principale et une autre dimension
        long principaleCount = indicateur.getIndicateurDimensions().stream()
                .filter(dim -> Boolean.TRUE.equals(dim.getPrincipale()))
                .count();

        long autresCount = indicateur.getIndicateurDimensions().stream()
                .filter(dim -> !Boolean.TRUE.equals(dim.getPrincipale()))
                .count();

        return principaleCount >= 1 && autresCount >= 1;
    }
}
