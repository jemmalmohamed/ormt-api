package ma.org.ancfcc.pva.modules.mission.photo.planification.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import ma.org.ancfcc.pva.core.commun.base.service.BaseServiceImpl;
import ma.org.ancfcc.pva.core.commun.base.service.SpecificationService;
import ma.org.ancfcc.pva.core.gis.utils.GeometryCreation;
import ma.org.ancfcc.pva.core.validators.ObjectsValidator;
import ma.org.ancfcc.pva.modules.mission.bande.Bande;
import ma.org.ancfcc.pva.modules.mission.photo.planification.PhotoPlanification;
import ma.org.ancfcc.pva.modules.mission.photo.planification.dto.request.PhotoPlanificationRequestDto;
import ma.org.ancfcc.pva.modules.mission.photo.planification.dto.request.PhotoPlanificationRequestDtoMapper;
import ma.org.ancfcc.pva.modules.mission.photo.planification.repository.PhotoPlanificationRepository;
import ma.org.ancfcc.pva.modules.mission.service.planification.helper.MissionPlanificationHelper;
import ma.org.ancfcc.pva.modules.mission.service.planification.xml.parser.PlanEventInfo;

@Service
public class PhotoPlanificationServiceImpl extends BaseServiceImpl<PhotoPlanification>
        implements PhotoPlanificationService {

    @Autowired
    private PhotoPlanificationRepository photoPlanificationRepository;

    @Autowired
    private MissionPlanificationHelper missionPlanificationHelper;

    @Autowired
    private ObjectsValidator<PhotoPlanificationRequestDto> validator;

    @Autowired
    private PhotoPlanificationRequestDtoMapper photoPlanificationRequestMapper;

    public PhotoPlanificationServiceImpl(PhotoPlanificationRepository photoPlanificationRepository,
            SpecificationService specificationService) {
        super(photoPlanificationRepository, specificationService);
    }

    /**
     * Updates an Photo with new values
     *
     * @param id           the ID of the Photo to update
     * @param updatedPhoto the new Photo data
     * @return the updated Photo
     */
    @Override
    // @Transactional
    public PhotoPlanification update(Long id, PhotoPlanificationRequestDto photoPlanificationRequestDto) {
        validator.validate(photoPlanificationRequestDto);

        PhotoPlanification photoPlanificationToUpdate = photoPlanificationRequestMapper
                .mapToEntity(photoPlanificationRequestDto);

        checkPathId(id, photoPlanificationToUpdate.getId());

        PhotoPlanification photoPlanification = photoPlanificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Photo not found"));

        return photoPlanificationRepository.save(photoPlanification);
    }

    @Override
    // @Transactional
    public PhotoPlanification create(PhotoPlanificationRequestDto requestDto) {

        validator.validate(requestDto);

        PhotoPlanification photoPlanificationToCreate = photoPlanificationRequestMapper.mapToEntity(requestDto);

        PhotoPlanification photoPlanification = create(photoPlanificationToCreate);

        return photoPlanificationRepository.save(photoPlanification);
    }

    @Override
    public List<PhotoPlanification> savePhotoPlanificationFromXml(List<PlanEventInfo> planEventInfos, Bande bande,
            Integer srid) {
        List<PhotoPlanification> photos = new ArrayList<>();
        for (PlanEventInfo planEventInfo : planEventInfos) {
            PhotoPlanification photo = new PhotoPlanification();
            Point centre = GeometryCreation.createPoint(planEventInfo.getPosition(), srid);
            photo.setCentre(centre);
            photo.setBande(bande);

            photo.setNom(missionPlanificationHelper.formaLabel(planEventInfo.getPlanEventLabel()));
            photo.setLabel(planEventInfo.getPlanEventLabel());
            photos.add(photo);
        }
        return photoPlanificationRepository.saveAll(photos);
    }

    @Override
    public Long countByBandeId(Long bandeId) {
        return photoPlanificationRepository.countByBandeId(bandeId);
    }

    @Override
    public Optional<PhotoPlanification> findPhotoPlanificationByLabelAndBandeId(String label, Long bandeId) {
        return photoPlanificationRepository.findPhotoPlanificationByLabelAndBandeId(label, bandeId);
    }

    @Override
    public Optional<PhotoPlanification> findPhotoPlanificationByNomAndBandeNomAndMissionCode(
            String photoPlanificationNom, String bandeNom, String missionCode) {
        return photoPlanificationRepository.findPhotoPlanificationByNomAndBandeNomAndMissionCode(photoPlanificationNom,
                bandeNom, missionCode);
    }

}