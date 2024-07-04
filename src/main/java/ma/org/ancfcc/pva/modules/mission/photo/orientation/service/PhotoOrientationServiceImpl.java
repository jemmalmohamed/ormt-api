package ma.org.ancfcc.pva.modules.mission.photo.orientation.service;

import java.util.UUID;

import org.geotools.api.feature.simple.SimpleFeature;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ma.org.ancfcc.pva.core.gis.shapefile.ShpSimpleFeatureService;

import jakarta.persistence.EntityNotFoundException;
import ma.org.ancfcc.pva.core.commun.base.service.BaseServiceImpl;
import ma.org.ancfcc.pva.core.commun.base.service.SpecificationService;
import ma.org.ancfcc.pva.core.validators.ObjectsValidator;
import ma.org.ancfcc.pva.modules.mission.photo.orientation.PhotoOrientation;
import ma.org.ancfcc.pva.modules.mission.photo.orientation.dto.request.PhotoOrientationRequestDto;
import ma.org.ancfcc.pva.modules.mission.photo.orientation.dto.request.PhotoOrientationRequestDtoMapper;
import ma.org.ancfcc.pva.modules.mission.photo.orientation.repository.PhotoOrientationRepository;
import ma.org.ancfcc.pva.modules.mission.photo.planification.PhotoPlanification;
import ma.org.ancfcc.pva.core.gis.utils.GeometryUtils;

@Service
public class PhotoOrientationServiceImpl extends BaseServiceImpl<PhotoOrientation>
        implements PhotoOrientationService {

    @Autowired
    private PhotoOrientationRepository photoOrientationRepository;

    @Autowired
    private ObjectsValidator<PhotoOrientationRequestDto> validator;

    @Autowired
    private PhotoOrientationRequestDtoMapper photoOrientationRequestMapper;

    public PhotoOrientationServiceImpl(PhotoOrientationRepository photoOrientationRepository,
            SpecificationService specificationService) {
        super(photoOrientationRepository, specificationService);
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
    public PhotoOrientation update(UUID id, PhotoOrientationRequestDto photoOrientationRequestDto) {
        validator.validate(photoOrientationRequestDto);

        PhotoOrientation photoOrientationToUpdate = photoOrientationRequestMapper
                .mapToEntity(photoOrientationRequestDto);

        checkPathId(id, photoOrientationToUpdate.getId());

        PhotoOrientation photoOrientation = photoOrientationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Photo not found"));

        return photoOrientationRepository.save(photoOrientation);
    }

    @Override
    // @Transactional
    public PhotoOrientation create(PhotoOrientationRequestDto requestDto) {

        validator.validate(requestDto);

        PhotoOrientation photoOrientationToCreate = photoOrientationRequestMapper.mapToEntity(requestDto);

        PhotoOrientation photoOrientation = create(photoOrientationToCreate);

        return photoOrientationRepository.save(photoOrientation);
    }

    @Override
    public PhotoOrientation savePhotoOrientationFromShpFeature(PhotoPlanification photoPlanification,
            SimpleFeature featurePhoto, Integer srid) {

        Point centre = GeometryUtils.extract2DPointFromFeature(featurePhoto, srid);
        PhotoOrientation photoOrientation = new PhotoOrientation();

        String altitude = ShpSimpleFeatureService.getValueFromFeature(featurePhoto, "Altitude");
        String kappa = ShpSimpleFeatureService.getValueFromFeature(featurePhoto, "Kappa");
        String omega = ShpSimpleFeatureService.getValueFromFeature(featurePhoto, "Omega");
        String phi = ShpSimpleFeatureService.getValueFromFeature(featurePhoto, "Phi");
        String tempsGps = ShpSimpleFeatureService.getValueFromFeature(featurePhoto, "Temps GPS");
        String geoidModel = ShpSimpleFeatureService.getValueFromFeature(featurePhoto, "Altitude_G");

        photoOrientation.setPhotoPlanification(photoPlanification);
        photoOrientation.setCentre(centre);
        photoOrientation.setAltitude(Float.parseFloat(altitude));
        photoOrientation.setKappa(Float.parseFloat(kappa));
        photoOrientation.setOmega(Float.parseFloat(omega));
        photoOrientation.setPhi(Float.parseFloat(phi));
        photoOrientation.setTempsGps(Float.parseFloat(tempsGps));
        photoOrientation.setGeoidModel(geoidModel);
        return create(photoOrientation);

    }

}