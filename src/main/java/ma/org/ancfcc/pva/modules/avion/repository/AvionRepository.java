package ma.org.ancfcc.pva.modules.avion.repository;

import java.util.Optional;

import ma.org.ancfcc.pva.core.commun.base.repository.BaseRepository;
import ma.org.ancfcc.pva.modules.avion.Avion;

public interface AvionRepository extends BaseRepository<Avion> {

    Optional<Avion> findByMatricule(String matricule);

}