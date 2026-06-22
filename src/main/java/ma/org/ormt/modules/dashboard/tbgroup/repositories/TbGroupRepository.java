package ma.org.ormt.modules.dashboard.tbgroup.repositories;

import java.util.Optional;
import org.springframework.stereotype.Repository;
import ma.org.ormt.core.commun.base.repository.BaseRepository;
import ma.org.ormt.modules.dashboard.tbgroup.models.TbGroup;

@Repository
public interface TbGroupRepository extends BaseRepository<TbGroup> {

    Optional<TbGroup> findByNom(String nom);

}
