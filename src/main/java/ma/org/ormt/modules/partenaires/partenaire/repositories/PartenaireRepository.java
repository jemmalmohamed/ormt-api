package ma.org.ormt.modules.partenaires.partenaire.repositories;

import java.util.Optional;

import ma.org.ormt.core.commun.base.repository.BaseRepository;
import ma.org.ormt.modules.partenaires.partenaire.models.Partenaire;

public interface PartenaireRepository extends BaseRepository<Partenaire> {

    Optional<Partenaire> findByNom(String nom);

}