package ma.org.ormt.modules.indicateurs.donnee.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ma.org.ormt.core.commun.base.repository.BaseRepository;
import ma.org.ormt.modules.indicateurs.donnee.models.DonneeIndicateur;

@Repository
public interface DonneeIndicateurRepository extends BaseRepository<DonneeIndicateur> {
    @EntityGraph(attributePaths = { "valeurDimensions", "valeurDimensions.dimension" })
    List<DonneeIndicateur> findAllByIndicateurId(Long indicateurId);

    @Modifying
    @Query("DELETE FROM DonneeIndicateur di WHERE di.indicateur.id = :indicateurId")
    void deleteAllByIndicateurId(@Param("indicateurId") Long indicateurId);

}
