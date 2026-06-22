package ma.org.ormt.modules.domaines.domaine.repositories;

import java.util.Optional;

import ma.org.ormt.core.commun.base.repository.BaseRepository;
import ma.org.ormt.modules.domaines.domaine.models.Domaine;

public interface DomaineRepository extends BaseRepository<Domaine> {

    Optional<Domaine> findByNom(String nom);

}
