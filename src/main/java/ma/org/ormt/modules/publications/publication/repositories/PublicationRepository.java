package ma.org.ormt.modules.publications.publication.repositories;

import ma.org.ormt.modules.publications.publication.models.Publication;
import ma.org.ormt.core.commun.base.repository.BaseRepository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

@Repository
public interface PublicationRepository extends BaseRepository<Publication> {

    Optional<Publication> findByTitre(String titre);

}
