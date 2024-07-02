package ma.org.ancfcc.pva.modules.mission.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ma.org.ancfcc.pva.core.commun.base.repository.BaseRepository;
import ma.org.ancfcc.pva.modules.mission.models.Mission;

public interface MissionRepository extends BaseRepository<Mission> {

    Optional<Mission> findByCode(String code);

    Optional<Mission> findByNom(String nom);

    boolean existsByCode(String code);

    boolean existsByIdAndDelimitationIsNotNull(UUID id);

    @Query("SELECT COUNT(p) FROM PhotoPlanification p JOIN p.bande b JOIN b.mission m WHERE m.id = :missionId")
    long countPhotoPlanificationsByMissionId(@Param("missionId") UUID missionId);

    @Query("SELECT COUNT(b) FROM Bande b JOIN b.mission m WHERE m.id = :missionId")
    long countByBandesMissionId(@Param("missionId") UUID missionId);

}