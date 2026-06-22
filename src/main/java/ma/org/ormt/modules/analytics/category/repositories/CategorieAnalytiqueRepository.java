package ma.org.ormt.modules.analytics.category.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import ma.org.ormt.core.commun.base.repository.BaseRepository;
import ma.org.ormt.modules.analytics.category.models.CategorieAnalytique;

@Repository
public interface CategorieAnalytiqueRepository extends BaseRepository<CategorieAnalytique> {

    List<CategorieAnalytique> findByDomaineAnalytiqueIdOrderByOrdreAscLibelleAsc(Long domaineAnalytiqueId);

    Optional<CategorieAnalytique> findByDomaineAnalytiqueIdAndNom(Long domaineAnalytiqueId, String nom);

    boolean existsByIdAndDomaineAnalytiqueId(Long id, Long domaineAnalytiqueId);
}
