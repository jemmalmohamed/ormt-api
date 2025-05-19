package ma.org.ormt.modules.chiffres.repositories;

import java.util.Optional;
import org.springframework.stereotype.Repository;
import ma.org.ormt.core.commun.base.repository.BaseRepository;
import ma.org.ormt.modules.chiffres.models.ChiffreCle;

@Repository
public interface ChiffreCleRepository extends BaseRepository<ChiffreCle> {
    Optional<ChiffreCle> findByLibelle(String libelle);
}