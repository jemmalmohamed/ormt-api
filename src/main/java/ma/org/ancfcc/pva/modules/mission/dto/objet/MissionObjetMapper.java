package ma.org.ancfcc.pva.modules.mission.dto.objet;

import org.mapstruct.Mapper;

import ma.org.ancfcc.pva.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ancfcc.pva.modules.mission.Mission;

@Mapper
public interface MissionObjetMapper extends BaseDtoMapper<Mission, MissionObjetDto> {

}
