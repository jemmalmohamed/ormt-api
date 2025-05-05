package ma.org.ormt.modules.indicateurs.indicateur.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ma.org.ormt.core.commun.base.repository.BaseRepository;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;

public interface IndicateurRepository extends BaseRepository<Indicateur> {
    Optional<Indicateur> findByNom(String nom);

    // @Query("SELECT i FROM Indicateur i " +
    // "LEFT JOIN FETCH i.indicateurDimensions id " +
    // "LEFT JOIN FETCH id.dimension d " +
    // "LEFT JOIN FETCH i.donnees donnees " +
    // "LEFT JOIN FETCH donnees.valeursDimension vd " +
    // "LEFT JOIN FETCH vd.dimension " +
    // "WHERE i.id = :id")
    // Optional<Indicateur> findByIdWithDonneesAndDimensions(@Param("id") Long id);
}