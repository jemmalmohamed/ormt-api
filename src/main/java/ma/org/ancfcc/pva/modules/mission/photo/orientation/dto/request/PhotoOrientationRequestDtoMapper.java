package ma.org.ancfcc.pva.modules.mission.photo.orientation.dto.request;

import org.mapstruct.Mapper;

import ma.org.ancfcc.pva.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ancfcc.pva.modules.mission.photo.orientation.PhotoOrientation;

@Mapper
public interface PhotoOrientationRequestDtoMapper
                extends BaseDtoMapper<PhotoOrientation, PhotoOrientationRequestDto> {

}
