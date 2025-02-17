package ma.org.ormt.modules.indicateurs.donnee.repositories;

import org.springframework.stereotype.Repository;

import ma.org.ormt.core.commun.base.repository.BaseRepository;
import ma.org.ormt.modules.indicateurs.donnee.models.DonneeIndicateur;

@Repository
public interface DonneeIndicateurRepository extends BaseRepository<DonneeIndicateur> {

}