package ma.org.ormt.modules.indicateurs.indicateur.services.export.helpers.utils;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;

/**
 * Service responsable du filtrage des indicateurs pour l'export
 */
@Service
@Slf4j
public class IndicateurExportFilterService {

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
                        .groupingBy(
                                ind -> ind.getSource() != null && StringUtils.hasText(ind.getSource().getAbreviation())
                                        ? ind.getSource().getAbreviation()
                                        : "Sans source"));
    }
}
