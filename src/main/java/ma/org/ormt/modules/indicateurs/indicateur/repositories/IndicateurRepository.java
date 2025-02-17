package ma.org.ormt.modules.indicateurs.indicateur.repositories;

import java.util.Optional;

import ma.org.ormt.core.commun.base.repository.BaseRepository;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;

public interface IndicateurRepository extends BaseRepository<Indicateur> {
    Optional<Indicateur> findByNom(String nom);

}