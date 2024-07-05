package ma.org.ormt.modules.mission.photo.planification.dto.request;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.mission.photo.planification.PhotoPlanification;

@Mapper
public interface PhotoPlanificationRequestDtoMapper
                extends BaseDtoMapper<PhotoPlanification, PhotoPlanificationRequestDto> {

}
