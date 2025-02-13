package ma.org.ormt.modules.sousdomaine.repository;

import java.util.Optional;

import ma.org.ormt.core.commun.base.repository.BaseRepository;
import ma.org.ormt.modules.sousdomaine.SousDomaine;

public interface SousDomaineRepository extends BaseRepository<SousDomaine> {

    Optional<SousDomaine> findByTitre(String titre);

}