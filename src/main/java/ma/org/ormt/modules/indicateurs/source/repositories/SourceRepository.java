package ma.org.ormt.modules.indicateurs.source.repositories;

import java.util.Optional;
import org.springframework.stereotype.Repository;
import ma.org.ormt.core.commun.base.repository.BaseRepository;
import ma.org.ormt.modules.indicateurs.source.models.Source;

@Repository
public interface SourceRepository extends BaseRepository<Source> {
    Optional<Source> findByAbreviation(String abreviation);

    Optional<Source> findByNomIgnoreCase(String nom);

    boolean existsByNomIgnoreCase(String nom);

    boolean existsByNomIgnoreCaseAndIdNot(String nom, Long id);
}