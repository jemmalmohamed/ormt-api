package ma.org.ormt.modules.hcp.chomage.diplome_milieu.repository;

import java.util.Optional;

import ma.org.ormt.core.commun.base.repository.BaseRepository;
import ma.org.ormt.modules.hcp.chomage.diplome_milieu.DiplomeMilieu;

public interface DiplomeMilieuRepository extends BaseRepository<DiplomeMilieu> {

    Optional<DiplomeMilieu> findByAnnee(String annee);

}