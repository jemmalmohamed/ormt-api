package ma.org.ormt.modules.indicateurs.indicateur.association.dimension.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ma.org.ormt.core.commun.base.repository.BaseRepository;
import ma.org.ormt.modules.indicateurs.indicateur.association.dimension.models.IndicateurDimension;

public interface IndicateurDimensionRepository extends BaseRepository<IndicateurDimension> {

    boolean existsByIndicateurIdAndDimensionId(Long indicateurId, Long dimensionId);

    @Query("""
            SELECT COUNT(id) > 0
            FROM IndicateurDimension id
            WHERE id.indicateur.id = :indicateurId
            AND id.principale = true
            AND (:excludeId IS NULL OR id.id <> :excludeId)
            """)
    boolean existsPrincipaleForIndicateur(
            @Param("indicateurId") Long indicateurId,
            @Param("excludeId") Long excludeId);

    @Query("""
            SELECT COUNT(id) > 0
            FROM IndicateurDimension id
            WHERE id.indicateur.id = :indicateurId
            AND id.temporelle = true
            AND (:excludeId IS NULL OR id.id <> :excludeId)
            """)
    boolean existsTemporelleForIndicateur(
            @Param("indicateurId") Long indicateurId,
            @Param("excludeId") Long excludeId);

}
