package ma.org.ancfcc.pva.modules.capteur.repository;

import java.util.Optional;

import ma.org.ancfcc.pva.core.commun.base.repository.BaseRepository;
import ma.org.ancfcc.pva.modules.capteur.Capteur;

public interface CapteurRepository extends BaseRepository<Capteur> {

    Optional<Capteur> findByNom(String nom);

    Optional<Capteur> findByCode(String code);

}