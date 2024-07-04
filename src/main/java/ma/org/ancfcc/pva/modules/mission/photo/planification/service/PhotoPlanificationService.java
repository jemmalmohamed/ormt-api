package ma.org.ancfcc.pva.modules.mission.photo.planification.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import ma.org.ancfcc.pva.core.commun.base.service.BaseService;
import ma.org.ancfcc.pva.modules.mission.bande.Bande;
import ma.org.ancfcc.pva.modules.mission.photo.planification.PhotoPlanification;
import ma.org.ancfcc.pva.modules.mission.photo.planification.dto.request.PhotoPlanificationRequestDto;
import ma.org.ancfcc.pva.modules.mission.service.planification.xml.parser.PlanEventInfo;

public interface PhotoPlanificationService extends BaseService<PhotoPlanification> {

    PhotoPlanification create(PhotoPlanificationRequestDto requestDto);

    PhotoPlanification update(UUID id, PhotoPlanificationRequestDto photoRequestDto);

    List<PhotoPlanification> savePhotoPlanificationFromXml(List<PlanEventInfo> planEventInfos, Bande bande,
            Integer srid);

    Optional<PhotoPlanification> findPhotoPlanificationByLabelAndBandeId(String label, UUID bandeId);

    Optional<PhotoPlanification> findPhotoPlanificationByNomAndBandeNomAndMissionCode(String photoPlanificationNom,
            String bandeNom, String missionCode);

    Long countByBandeId(UUID bandeId);
}