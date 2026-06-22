package ma.org.ormt.modules.analytics.domain.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ma.org.ormt.core.commun.base.repository.BaseRepository;
import ma.org.ormt.modules.analytics.domain.models.DomaineAnalytique;

@Repository
public interface DomaineAnalytiqueRepository extends BaseRepository<DomaineAnalytique> {

    Optional<DomaineAnalytique> findByNom(String nom);

    Optional<DomaineAnalytique> findBySlug(String slug);

    Optional<DomaineAnalytique> findBySourceThemeKey(String sourceThemeKey);

    List<DomaineAnalytique> findByActifTrueOrderByTitreAsc();

    boolean existsByIdAndEspaceDomainesAnalytiquesEspaceId(Long id, Long espaceId);

    boolean existsByIdAndTbGroupDomainesAnalytiquesTbGroupId(Long id, Long tbGroupId);

    @Query("select da.id from DomaineAnalytique da join da.espaceDomainesAnalytiques eda where eda.espace.id = :espaceId order by eda.ordre asc")
    List<Long> findIdsByEspaceId(@Param("espaceId") Long espaceId);

    @Query("select da.id from DomaineAnalytique da join da.tbGroupDomainesAnalytiques tda where tda.tbGroup.id = :tbGroupId order by tda.ordre asc")
    List<Long> findIdsByTbGroupId(@Param("tbGroupId") Long tbGroupId);
}
