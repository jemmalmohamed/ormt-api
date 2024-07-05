package ma.org.ancfcc.pva.modules.mission.scan.dto;

import org.mapstruct.Mapper;

import ma.org.ancfcc.pva.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ancfcc.pva.modules.mission.scan.ScanExecution;

@Mapper
public interface ScanExecutionDtoMapper extends BaseDtoMapper<ScanExecution, ScanExecutionDto> {

}
