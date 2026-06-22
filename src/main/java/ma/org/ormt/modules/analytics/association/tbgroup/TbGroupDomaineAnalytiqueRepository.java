package ma.org.ormt.modules.analytics.association.tbgroup;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import ma.org.ormt.core.commun.base.repository.BaseRepository;

@Repository
public interface TbGroupDomaineAnalytiqueRepository extends BaseRepository<TbGroupDomaineAnalytique> {

    List<TbGroupDomaineAnalytique> findByTbGroupIdOrderByOrdreAsc(Long tbGroupId);

    Optional<TbGroupDomaineAnalytique> findByTbGroupIdAndDomaineAnalytiqueId(Long tbGroupId,
            Long domaineAnalytiqueId);
}
