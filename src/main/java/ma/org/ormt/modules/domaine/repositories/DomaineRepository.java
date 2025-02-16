package ma.org.ormt.modules.domaine.repositories;

import java.util.Optional;

import ma.org.ormt.core.commun.base.repository.BaseRepository;
import ma.org.ormt.modules.domaine.models.Domaine;

public interface DomaineRepository extends BaseRepository<Domaine> {

    Optional<Domaine> findByTitre(String titre);

}