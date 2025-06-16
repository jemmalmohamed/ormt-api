package ma.org.ormt.modules.indicateurs.indicateur.services.export.helpers.utils;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.dtos.IndicateurExportRequestDto;
import lombok.extern.slf4j.Slf4j;

/**
 * Service responsable du filtrage des indicateurs pour l'export
 */
@Service
@Slf4j
public class IndicateurExportFilterService {

    /**
     * Filtre les indicateurs selon les critères spécifiés
     */
    public List<Indicateur> filterIndicateurs(List<Indicateur> indicateurs, IndicateurExportRequestDto exportRequest) {
        if (indicateurs == null || indicateurs.isEmpty()) {
            log.warn("Liste d'indicateurs vide ou null pour le filtrage");
            return indicateurs;
        }

        List<Indicateur> filtered = indicateurs.stream()
                .filter(this::isValidIndicateur)
                .collect(Collectors.toList());

        // Appliquer le filtre "actifs seulement" si demandé
        if (exportRequest.isActiveOnly()) {
            filtered = filtered.stream()
                    .filter(ind -> Boolean.TRUE.equals(ind.getActif()))
                    .collect(Collectors.toList());
            log.debug("Filtrage des indicateurs actifs: {} indicateurs retenus sur {}",
                    filtered.size(), indicateurs.size());
        }

        return filtered;
    }

    /**
     * Valide qu'un indicateur est valide pour l'export
     */
    private boolean isValidIndicateur(Indicateur indicateur) {
        if (indicateur == null) {
            log.warn("Indicateur null trouvé dans la liste");
            return false;
        }

        if (indicateur.getId() == null) {
            log.warn("Indicateur sans ID trouvé: {}", indicateur.getNom());
            return false;
        }

        return true;
    }

    /**
     * Vérifie si la liste filtrée est vide
     */
    public boolean isEmpty(List<Indicateur> indicateurs) {
        return indicateurs == null || indicateurs.isEmpty();
    }

    /**
     * Compte les indicateurs actifs dans une liste
     */
    public long countActiveIndicateurs(List<Indicateur> indicateurs) {
        if (indicateurs == null || indicateurs.isEmpty()) {
            return 0;
        }

        return indicateurs.stream()
                .filter(ind -> ind != null && Boolean.TRUE.equals(ind.getActif()))
                .count();
    }

    /**
     * Groupe les indicateurs par domaine
     */
    public java.util.Map<String, List<Indicateur>> groupByDomaine(List<Indicateur> indicateurs) {
        return indicateurs.stream()
                .collect(Collectors.groupingBy(ind -> {
                    if (ind.getSousDomaines() == null || ind.getSousDomaines().isEmpty()) {
                        return "Sans domaine";
                    }
                    return ind.getSousDomaines().stream()
                            .map(sd -> sd.getDomaine() != null ? sd.getDomaine().getNom() : "Domaine inconnu")
                            .collect(Collectors.joining(", "));
                }));
    }

    /**
     * Groupe les indicateurs par source
     */
    public java.util.Map<String, List<Indicateur>> groupBySource(List<Indicateur> indicateurs) {
        return indicateurs.stream()
                .collect(Collectors
                        .groupingBy(ind -> ind.getSource() != null && StringUtils.hasText(ind.getSource().getNom())
                                ? ind.getSource().getNom()
                                : "Sans source"));
    }
}
