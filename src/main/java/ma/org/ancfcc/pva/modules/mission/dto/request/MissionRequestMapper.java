package ma.org.ancfcc.pva.modules.mission.dto.request;

import org.mapstruct.Mapper;

import ma.org.ancfcc.pva.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ancfcc.pva.modules.mission.models.Mission;

@Mapper
public interface MissionRequestMapper extends BaseDtoMapper<Mission, MissionRequestDto> {

}
