package ma.org.ormt.modules.avion.repository;

import java.util.Optional;

import ma.org.ormt.core.commun.base.repository.BaseRepository;
import ma.org.ormt.modules.avion.Avion;

public interface AvionRepository extends BaseRepository<Avion> {

    Optional<Avion> findByMatricule(String matricule);

}