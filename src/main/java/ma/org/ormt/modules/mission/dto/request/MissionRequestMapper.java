package ma.org.ormt.modules.mission.dto.request;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.mission.models.Mission;

@Mapper
public interface MissionRequestMapper extends BaseDtoMapper<Mission, MissionRequestDto> {

}
