package ma.org.ormt.modules.mission.dto;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.mission.models.Mission;

@Mapper
public interface MissionDtoMapper extends BaseDtoMapper<Mission, MissionDto> {

}
