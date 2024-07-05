package ma.org.ancfcc.pva.modules.mission.photo.planification.dto;

import org.mapstruct.Mapper;

import ma.org.ancfcc.pva.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ancfcc.pva.modules.mission.photo.planification.PhotoPlanification;

@Mapper
public interface PhotoPlanificationDtoMapper extends BaseDtoMapper<PhotoPlanification, PhotoPlanificationDto> {

}
