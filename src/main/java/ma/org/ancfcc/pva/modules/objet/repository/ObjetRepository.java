package ma.org.ancfcc.pva.modules.objet.repository;

import java.util.Optional;

import ma.org.ancfcc.pva.core.commun.base.repository.BaseRepository;
import ma.org.ancfcc.pva.modules.objet.Objet;

public interface ObjetRepository extends BaseRepository<Objet> {

    Optional<Objet> findByNom(String nom);

}