package ma.org.ancfcc.pva.modules.mission.photo.execution.dto.request;

import org.mapstruct.Mapper;

import ma.org.ancfcc.pva.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ancfcc.pva.modules.mission.photo.execution.PhotoExecution;

@Mapper
public interface PhotoExecutionRequestDtoMapper
                extends BaseDtoMapper<PhotoExecution, PhotoExecutionRequestDto> {

}
