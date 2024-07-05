package ma.org.ancfcc.pva.modules.mission.photo.execution.service;

import org.geotools.api.feature.simple.SimpleFeature;

import ma.org.ancfcc.pva.core.commun.base.service.BaseService;
import ma.org.ancfcc.pva.modules.mission.photo.execution.PhotoExecution;
import ma.org.ancfcc.pva.modules.mission.photo.execution.dto.request.PhotoExecutionRequestDto;
import ma.org.ancfcc.pva.modules.mission.photo.planification.PhotoPlanification;

public interface PhotoExecutionService extends BaseService<PhotoExecution> {

    PhotoExecution create(PhotoExecutionRequestDto requestDto);

    PhotoExecution update(Long id, PhotoExecutionRequestDto photoRequestDto);

    PhotoExecution savePhotoExecution(PhotoPlanification photoPlanification, SimpleFeature featurePhoto);

}