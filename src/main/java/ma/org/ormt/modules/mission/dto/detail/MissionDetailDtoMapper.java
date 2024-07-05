package ma.org.ormt.modules.mission.dto.detail;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.mission.models.Mission;

@Mapper
public interface MissionDetailDtoMapper extends BaseDtoMapper<Mission, MissionDetailDto> {

}
