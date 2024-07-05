package ma.org.ormt.modules.mission.photo.orientation.dto.request;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.mission.photo.orientation.PhotoOrientation;

@Mapper
public interface PhotoOrientationRequestDtoMapper
                extends BaseDtoMapper<PhotoOrientation, PhotoOrientationRequestDto> {

}
