package ma.org.ormt.modules.espaces.repositories;

import java.util.Optional;
import org.springframework.stereotype.Repository;
import ma.org.ormt.core.commun.base.repository.BaseRepository;
import ma.org.ormt.modules.espaces.models.Espace;

@Repository
public interface EspaceRepository extends BaseRepository<Espace> {
    Optional<Espace> findByNom(String nom);
}