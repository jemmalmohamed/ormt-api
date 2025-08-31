package ma.org.ormt.modules.dashboard.tableaubord.association.domaine.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ma.org.ormt.core.commun.base.repository.BaseRepository;
import ma.org.ormt.modules.dashboard.tableaubord.association.domaine.TableauBordDomaine;

public interface TableauBordDomaineRepository extends BaseRepository<TableauBordDomaine> {

    @Query("SELECT ed FROM TableauBordDomaine ed WHERE ed.tableauBord.id = :tableaubordId ORDER BY ed.ordre ASC")
    List<TableauBordDomaine> findByTableauBordIdOrderByOrdreAsc(@Param("tableaubordId") Long tableaubordId);
}