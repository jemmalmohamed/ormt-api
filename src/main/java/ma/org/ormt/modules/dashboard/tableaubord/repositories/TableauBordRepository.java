package ma.org.ormt.modules.dashboard.tableaubord.repositories;

import java.util.Optional;
import org.springframework.stereotype.Repository;
import ma.org.ormt.core.commun.base.repository.BaseRepository;
import ma.org.ormt.modules.dashboard.tableaubord.models.TableauBord;

@Repository
public interface TableauBordRepository extends BaseRepository<TableauBord> {

    Optional<TableauBord> findByNom(String nom);

}
