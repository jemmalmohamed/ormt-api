package ma.org.ormt.modules.mission.photo.execution.service;

import java.time.LocalDate;

import org.geotools.api.feature.simple.SimpleFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import ma.org.ormt.core.commun.base.service.BaseServiceImpl;
import ma.org.ormt.core.commun.base.service.SpecificationService;
import ma.org.ormt.core.gis.shapefile.ShpSimpleFeatureService;
import ma.org.ormt.core.utilities.DateUtils;
import ma.org.ormt.core.validators.ObjectsValidator;
import ma.org.ormt.modules.mission.photo.execution.PhotoExecution;
import ma.org.ormt.modules.mission.photo.execution.dto.request.PhotoExecutionRequestDto;
import ma.org.ormt.modules.mission.photo.execution.dto.request.PhotoExecutionRequestDtoMapper;
import ma.org.ormt.modules.mission.photo.execution.repository.PhotoExecutionRepository;
import ma.org.ormt.modules.mission.photo.planification.PhotoPlanification;

@Service
public class PhotoExecutionServiceImpl extends BaseServiceImpl<PhotoExecution>
        implements PhotoExecutionService {

    @Autowired
    private PhotoExecutionRepository photoExecutionRepository;

    @Autowired
    private ObjectsValidator<PhotoExecutionRequestDto> validator;

    @Autowired
    private PhotoExecutionRequestDtoMapper photoExecutionRequestMapper;

    public PhotoExecutionServiceImpl(PhotoExecutionRepository photoExecutionRepository,
            SpecificationService specificationService) {
        super(photoExecutionRepository, specificationService);
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
    public PhotoExecution update(Long id, PhotoExecutionRequestDto photoExecutionRequestDto) {
        validator.validate(photoExecutionRequestDto);

        PhotoExecution photoExecutionToUpdate = photoExecutionRequestMapper
                .mapToEntity(photoExecutionRequestDto);

        checkPathId(id, photoExecutionToUpdate.getId());

        PhotoExecution photoExecution = photoExecutionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Photo not found"));

        return photoExecutionRepository.save(photoExecution);
    }

    @Override
    // @Transactional
    public PhotoExecution create(PhotoExecutionRequestDto requestDto) {

        validator.validate(requestDto);

        PhotoExecution photoExecutionToCreate = photoExecutionRequestMapper.mapToEntity(requestDto);

        PhotoExecution photoExecution = create(photoExecutionToCreate);

        return photoExecutionRepository.save(photoExecution);
    }

    @Override
    public PhotoExecution savePhotoExecution(PhotoPlanification photoPlanification, SimpleFeature feature) {

        PhotoExecution photoExecution = new PhotoExecution();

        String bobine = ShpSimpleFeatureService.getValueFromFeature(feature, "Bobine");
        String datePva = ShpSimpleFeatureService.getValueFromFeature(feature, "Date");
        LocalDate date = DateUtils.parseLocalDate(datePva, "dd-MM-yyyy");

        if (bobine != null) {
            photoExecution.setBobine(bobine);
        }
        photoExecution.setPhotoPlanification(photoPlanification);
        photoExecution.setDatePva(date);
        return create(photoExecution);
    }

}