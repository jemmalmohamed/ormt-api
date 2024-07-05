package ma.org.ancfcc.pva.modules.mission.dto;

import org.mapstruct.Mapper;

import ma.org.ancfcc.pva.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ancfcc.pva.modules.mission.models.Mission;

@Mapper
public interface MissionDtoMapper extends BaseDtoMapper<Mission, MissionDto> {

}
