package ma.org.ormt.modules.domaines.sousdomaine.repositories;

import java.util.List;
import java.util.Optional;

import ma.org.ormt.core.commun.base.repository.BaseRepository;
import ma.org.ormt.modules.domaines.sousdomaine.models.SousDomaine;

public interface SousDomaineRepository extends BaseRepository<SousDomaine> {

    Optional<SousDomaine> findByNom(String nom);

    List<SousDomaine> findByDomaineIdOrderByOrdreAsc(Long domaineId);

}