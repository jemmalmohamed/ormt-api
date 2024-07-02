package ma.org.ancfcc.pva.modules.mission.photo.planification.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ma.org.ancfcc.pva.core.commun.base.repository.BaseRepository;
import ma.org.ancfcc.pva.modules.mission.photo.planification.PhotoPlanification;

public interface PhotoPlanificationRepository extends BaseRepository<PhotoPlanification> {

    @Query("SELECT COUNT(p) FROM PhotoPlanification p WHERE p.bande.id = :bandeId")
    long countByBandeId(@Param("bandeId") UUID bandeId);
}