package ma.org.ormt.modules.espaces.association.domaine.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ma.org.ormt.core.commun.base.repository.BaseRepository;
import ma.org.ormt.modules.espaces.association.domaine.EspaceDomaine;

public interface EspaceDomaineRepository extends BaseRepository<EspaceDomaine> {

    @Query("SELECT ed FROM EspaceDomaine ed WHERE ed.espace.id = :espaceId ORDER BY ed.ordre ASC")
    List<EspaceDomaine> findByEspaceIdOrderByOrdreAsc(@Param("espaceId") Long espaceId);
}