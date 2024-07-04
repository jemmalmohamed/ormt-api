package ma.org.ancfcc.pva.modules.mission.photo.planification.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ma.org.ancfcc.pva.core.commun.base.repository.BaseRepository;
import ma.org.ancfcc.pva.modules.mission.photo.planification.PhotoPlanification;

public interface PhotoPlanificationRepository extends BaseRepository<PhotoPlanification> {

        @Query("SELECT COUNT(p) FROM PhotoPlanification p WHERE p.bande.id = :bandeId")
        long countByBandeId(@Param("bandeId") UUID bandeId);

        @Query("SELECT p FROM PhotoPlanification p WHERE p.label = :label AND p.bande.id = :bandeId")
        Optional<PhotoPlanification> findPhotoPlanificationByLabelAndBandeId(@Param("label") String label,
                        @Param("bandeId") UUID bandeId);

        @Query("SELECT p FROM PhotoPlanification p WHERE p.nom = :photoPlanificationNom AND p.bande.nom = :bandeNom AND p.bande.mission.id = :missionId")
        Optional<PhotoPlanification> findPhotoPlanificationByNomAndBandeNomAndMissionId(
                        @Param("photoPlanificationNom") String photoPlanificationNom,
                        @Param("bandeNom") String bandeNom,
                        @Param("missionId") UUID missionId);

        @Query("SELECT photo FROM PhotoPlanification photo WHERE photo.nom = :photoPlanificationNom AND photo.bande.nom = :bandeNom AND photo.bande.mission.code = :missionCode")
        Optional<PhotoPlanification> findPhotoPlanificationByNomAndBandeNomAndMissionCode(
                        @Param("photoPlanificationNom") String photoPlanificationNom,
                        @Param("bandeNom") String bandeNom,
                        @Param("missionCode") String missionCode);
}