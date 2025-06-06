package ma.org.ormt.modules.domaines.domaine.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ma.org.ormt.core.commun.base.repository.BaseRepository;
import ma.org.ormt.modules.domaines.domaine.models.Domaine;

public interface DomaineRepository extends BaseRepository<Domaine> {

    Optional<Domaine> findByNom(String nom);

    boolean existsByIdAndEspaceDomainesEspaceId(Long id, Long espaceId);

    @Query("select d.id from Domaine d join d.espaceDomaines ed where ed.espace.id = :espaceId")
    List<Long> findIdsByEspaceId(@Param("espaceId") Long espaceId);

}