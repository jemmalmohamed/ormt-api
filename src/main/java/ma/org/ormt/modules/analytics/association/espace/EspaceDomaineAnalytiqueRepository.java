package ma.org.ormt.modules.analytics.association.espace;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import ma.org.ormt.core.commun.base.repository.BaseRepository;

@Repository
public interface EspaceDomaineAnalytiqueRepository extends BaseRepository<EspaceDomaineAnalytique> {

    List<EspaceDomaineAnalytique> findByEspaceIdOrderByOrdreAsc(Long espaceId);

    Optional<EspaceDomaineAnalytique> findByEspaceIdAndDomaineAnalytiqueId(Long espaceId, Long domaineAnalytiqueId);
}
