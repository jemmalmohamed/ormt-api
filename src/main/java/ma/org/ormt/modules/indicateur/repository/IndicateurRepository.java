package ma.org.ormt.modules.indicateur.repository;

import java.util.Optional;

import ma.org.ormt.core.commun.base.repository.BaseRepository;
import ma.org.ormt.modules.indicateur.models.Indicateur;

public interface IndicateurRepository extends BaseRepository<Indicateur> {
    Optional<Indicateur> findByNom(String nom);

}