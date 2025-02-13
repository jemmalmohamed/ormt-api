package ma.org.ormt.modules.domaine.repository;

import java.util.Optional;

import ma.org.ormt.core.commun.base.repository.BaseRepository;
import ma.org.ormt.modules.domaine.Domaine;

public interface DomaineRepository extends BaseRepository<Domaine> {

    Optional<Domaine> findByTitre(String titre);

}