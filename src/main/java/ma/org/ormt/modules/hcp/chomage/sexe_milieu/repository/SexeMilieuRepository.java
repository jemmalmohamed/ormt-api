package ma.org.ormt.modules.hcp.chomage.sexe_milieu.repository;

import java.util.Optional;

import ma.org.ormt.core.commun.base.repository.BaseRepository;
import ma.org.ormt.modules.hcp.chomage.sexe_milieu.SexeMilieu;

public interface SexeMilieuRepository extends BaseRepository<SexeMilieu> {

    Optional<SexeMilieu> findByAnnee(String annee);

}