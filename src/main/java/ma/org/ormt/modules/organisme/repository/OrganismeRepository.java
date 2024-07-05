package ma.org.ormt.modules.organisme.repository;

import java.util.Optional;

import ma.org.ormt.core.commun.base.repository.BaseRepository;
import ma.org.ormt.modules.organisme.Organisme;

public interface OrganismeRepository extends BaseRepository<Organisme> {

    Optional<Organisme> findByNom(String nom);

}