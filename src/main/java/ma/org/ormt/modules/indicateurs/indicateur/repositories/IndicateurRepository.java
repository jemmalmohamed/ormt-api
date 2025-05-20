package ma.org.ormt.modules.indicateurs.indicateur.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ma.org.ormt.core.commun.base.repository.BaseRepository;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;

public interface IndicateurRepository extends BaseRepository<Indicateur> {
    Optional<Indicateur> findByNom(String nom);

    @Query("SELECT i FROM Indicateur i LEFT JOIN FETCH i.donnees WHERE i.nom = :nom")
    Optional<Indicateur> findByNomWithDonnees(@Param("nom") String nom);

}