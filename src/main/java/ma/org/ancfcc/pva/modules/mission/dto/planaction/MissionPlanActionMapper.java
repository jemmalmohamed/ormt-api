package ma.org.ancfcc.pva.modules.mission.dto.planaction;

import org.mapstruct.Mapper;

import ma.org.ancfcc.pva.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ancfcc.pva.modules.mission.Mission;

@Mapper
public interface MissionPlanActionMapper extends BaseDtoMapper<Mission, MissionPlanActionDto> {

}
