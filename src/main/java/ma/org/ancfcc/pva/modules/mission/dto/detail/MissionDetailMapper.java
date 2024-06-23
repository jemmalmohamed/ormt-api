package ma.org.ancfcc.pva.modules.mission.dto.detail;

import org.mapstruct.Mapper;

import ma.org.ancfcc.pva.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ancfcc.pva.modules.mission.Mission;

@Mapper
public interface MissionDetailMapper extends BaseDtoMapper<Mission, MissionDetailDto> {

}
