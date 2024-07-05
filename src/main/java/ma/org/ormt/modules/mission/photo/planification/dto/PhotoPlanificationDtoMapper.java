package ma.org.ormt.modules.mission.photo.planification.dto;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.mission.photo.planification.PhotoPlanification;

@Mapper
public interface PhotoPlanificationDtoMapper extends BaseDtoMapper<PhotoPlanification, PhotoPlanificationDto> {

}
