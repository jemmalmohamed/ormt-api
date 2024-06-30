package ma.org.ancfcc.pva.modules.mission.repository;

import java.util.Optional;
import java.util.UUID;

import ma.org.ancfcc.pva.core.commun.base.repository.BaseRepository;
import ma.org.ancfcc.pva.modules.mission.Mission;

public interface MissionRepository extends BaseRepository<Mission> {

    Optional<Mission> findByCode(String code);

    Optional<Mission> findByNom(String nom);

    boolean existsByCode(String code);

    boolean existsByIdAndDelimitationIsNotNull(UUID id);

}