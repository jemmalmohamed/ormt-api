package ma.org.ormt.modules.analytics.category.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ma.org.ormt.core.commun.base.repository.BaseRepository;
import ma.org.ormt.modules.analytics.category.models.CategorieAnalytique;

@Repository
public interface CategorieAnalytiqueRepository extends BaseRepository<CategorieAnalytique> {

    List<CategorieAnalytique> findByDomaineAnalytiqueIdOrderByOrdreAscLibelleAsc(Long domaineAnalytiqueId);

    Optional<CategorieAnalytique> findByDomaineAnalytiqueIdAndNom(Long domaineAnalytiqueId, String nom);

    boolean existsByIdAndDomaineAnalytiqueId(Long id, Long domaineAnalytiqueId);

    Optional<CategorieAnalytique> findByTbdDashboardId(Long tbdDashboardId);

    List<CategorieAnalytique> findByTbdDashboardIdIsNotNullOrderByLibelleAsc();

    @Query("""
            select distinct category
            from CategorieAnalytique category
            join fetch category.domaineAnalytique domain
            where category.tbdDashboard.id in :dashboardIds
            order by domain.titre asc, category.ordre asc, category.libelle asc
            """)
    List<CategorieAnalytique> findByTbdDashboardIdInWithDomain(@Param("dashboardIds") List<Long> dashboardIds);

    @Query("""
            select category
            from CategorieAnalytique category
            join fetch category.domaineAnalytique domain
            where category.actif = true
              and category.tbdDashboard is null
            order by domain.titre asc, category.ordre asc, category.libelle asc
            """)
    List<CategorieAnalytique> findActiveWithoutDashboardOrderByDomainAndCategory();
}
