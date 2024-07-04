package ma.org.ancfcc.pva.modules.mission.photo.orientation.service;

import java.util.UUID;

import org.geotools.api.feature.simple.SimpleFeature;

import ma.org.ancfcc.pva.core.commun.base.service.BaseService;
import ma.org.ancfcc.pva.modules.mission.photo.orientation.PhotoOrientation;
import ma.org.ancfcc.pva.modules.mission.photo.orientation.dto.request.PhotoOrientationRequestDto;
import ma.org.ancfcc.pva.modules.mission.photo.planification.PhotoPlanification;

public interface PhotoOrientationService extends BaseService<PhotoOrientation> {

    PhotoOrientation create(PhotoOrientationRequestDto requestDto);

    PhotoOrientation update(UUID id, PhotoOrientationRequestDto photoRequestDto);

    PhotoOrientation savePhotoOrientationFromShpFeature(PhotoPlanification photoPlanification,
            SimpleFeature featurePhoto, Integer srid);

}