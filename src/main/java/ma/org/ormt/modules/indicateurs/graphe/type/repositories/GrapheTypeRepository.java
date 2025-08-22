package ma.org.ormt.modules.indicateurs.graphe.type.repositories;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import ma.org.ormt.core.commun.base.repository.BaseRepository;
import ma.org.ormt.modules.indicateurs.graphe.type.models.GrapheType;

@Repository
public interface GrapheTypeRepository extends BaseRepository<GrapheType> {

    Optional<GrapheType> findByNom(String nom);

    Optional<GrapheType> findByCode(String code);

    List<GrapheType> findByCodeIn(Collection<String> codes);
}