package ma.org.ormt.modules.mission.photo.execution.dto;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.mission.photo.execution.PhotoExecution;

@Mapper
public interface PhotoExecutionDtoMapper extends BaseDtoMapper<PhotoExecution, PhotoExecutionDto> {

}
