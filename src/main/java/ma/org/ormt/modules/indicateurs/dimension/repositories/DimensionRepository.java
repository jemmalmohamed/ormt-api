package ma.org.ormt.modules.indicateurs.dimension.repositories;

import java.util.Optional;
import org.springframework.stereotype.Repository;
import ma.org.ormt.core.commun.base.repository.BaseRepository;
import ma.org.ormt.modules.indicateurs.dimension.models.Dimension;

@Repository
public interface DimensionRepository extends BaseRepository<Dimension> {
    Optional<Dimension> findByNom(String nom);
}