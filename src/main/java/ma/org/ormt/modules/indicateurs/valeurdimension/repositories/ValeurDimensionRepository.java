package ma.org.ormt.modules.indicateurs.valeurdimension.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ma.org.ormt.core.commun.base.repository.BaseRepository;
import ma.org.ormt.modules.indicateurs.valeurdimension.models.ValeurDimension;

@Repository
public interface ValeurDimensionRepository extends BaseRepository<ValeurDimension> {

    /**
     * Check if there are any ValeurDimension records associated with a specific
     * dimension
     * that have actual data (DonneeIndicateur)
     * 
     * @param dimensionId the ID of the dimension to check
     * @return true if there are data records using this dimension, false otherwise
     */
    @Query("SELECT COUNT(vd) > 0 FROM ValeurDimension vd " +
            "WHERE vd.dimension.id = :dimensionId " +
            "AND vd.donneeIndicateur IS NOT NULL")
    boolean existsByDimensionIdWithData(@Param("dimensionId") Long dimensionId);

    /**
     * Check if there are any ValeurDimension records associated with a specific
     * dimension and indicateur
     * that have actual data (DonneeIndicateur)
     * 
     * @param dimensionId  the ID of the dimension to check
     * @param indicateurId the ID of the indicateur to check
     * @return true if there are data records using this dimension-indicateur
     *         combination, false otherwise
     */
    @Query("SELECT COUNT(vd) > 0 FROM ValeurDimension vd " +
            "WHERE vd.dimension.id = :dimensionId " +
            "AND vd.donneeIndicateur.indicateur.id = :indicateurId " +
            "AND vd.donneeIndicateur IS NOT NULL")
    boolean existsByDimensionAndIndicateurWithData(@Param("dimensionId") Long dimensionId,
            @Param("indicateurId") Long indicateurId);

}