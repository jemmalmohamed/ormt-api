package ma.org.ormt.modules.analytics.association.tbgroup;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import ma.org.ormt.core.commun.base.repository.BaseRepository;

@Repository
public interface TableauBordDomaineAnalytiqueRepository extends BaseRepository<TableauBordDomaineAnalytique> {

    List<TableauBordDomaineAnalytique> findByTableauBordIdOrderByOrdreAsc(Long tableauBordId);

    Optional<TableauBordDomaineAnalytique> findByTableauBordIdAndDomaineAnalytiqueId(Long tableauBordId,
            Long domaineAnalytiqueId);
}
