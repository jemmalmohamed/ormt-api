package ma.org.ormt.modules.sousdomaine.repositories;

import java.util.Optional;

import ma.org.ormt.core.commun.base.repository.BaseRepository;
import ma.org.ormt.modules.sousdomaine.models.SousDomaine;

public interface SousDomaineRepository extends BaseRepository<SousDomaine> {

    Optional<SousDomaine> findByTitre(String titre);

}