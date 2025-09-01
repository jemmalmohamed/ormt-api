package ma.org.ormt.modules.dashboard.domaine.tbdomaine.association.indicateur.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ma.org.ormt.core.commun.base.repository.BaseRepository;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.association.indicateur.TBDomaineIndicateur;

public interface TBDomaineIndicateurRepository extends BaseRepository<TBDomaineIndicateur> {

    @Query("SELECT ed FROM TBDomaineIndicateur ed WHERE ed.tbDomaine.id = :tbDomaineId ORDER BY ed.ordre ASC")
    List<TBDomaineIndicateur> findByTBDomaineIdOrderByOrdreAsc(@Param("tbDomaineId") Long tbDomaineId);
}