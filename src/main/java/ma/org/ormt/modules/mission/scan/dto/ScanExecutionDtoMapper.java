package ma.org.ormt.modules.mission.scan.dto;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.mission.scan.ScanExecution;

@Mapper
public interface ScanExecutionDtoMapper extends BaseDtoMapper<ScanExecution, ScanExecutionDto> {

}
