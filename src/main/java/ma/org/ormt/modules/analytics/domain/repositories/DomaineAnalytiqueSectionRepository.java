package ma.org.ormt.modules.analytics.domain.repositories;

import java.util.List;

import org.springframework.stereotype.Repository;

import ma.org.ormt.core.commun.base.repository.BaseRepository;
import ma.org.ormt.modules.analytics.domain.models.DomaineAnalytiqueSection;

@Repository
public interface DomaineAnalytiqueSectionRepository extends BaseRepository<DomaineAnalytiqueSection> {

    List<DomaineAnalytiqueSection> findByDomaineAnalytiqueIdOrderByOrdreAsc(Long domaineAnalytiqueId);
}
