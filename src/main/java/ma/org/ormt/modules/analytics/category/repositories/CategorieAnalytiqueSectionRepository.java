package ma.org.ormt.modules.analytics.category.repositories;

import java.util.List;

import org.springframework.stereotype.Repository;

import ma.org.ormt.core.commun.base.repository.BaseRepository;
import ma.org.ormt.modules.analytics.category.models.CategorieAnalytiqueSection;

@Repository
public interface CategorieAnalytiqueSectionRepository extends BaseRepository<CategorieAnalytiqueSection> {

    List<CategorieAnalytiqueSection> findByCategorieAnalytiqueIdOrderByOrdreAsc(Long categorieAnalytiqueId);
}
