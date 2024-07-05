package ma.org.ormt.modules.mission.photo.execution.dto.request;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.mission.photo.execution.PhotoExecution;

@Mapper
public interface PhotoExecutionRequestDtoMapper
                extends BaseDtoMapper<PhotoExecution, PhotoExecutionRequestDto> {

}
