package ma.org.ancfcc.pva.modules.organisme.repository;

import java.util.Optional;

import ma.org.ancfcc.pva.core.commun.base.repository.BaseRepository;
import ma.org.ancfcc.pva.modules.organisme.Organisme;

public interface OrganismeRepository extends BaseRepository<Organisme> {

    Optional<Organisme> findByNom(String nom);

}