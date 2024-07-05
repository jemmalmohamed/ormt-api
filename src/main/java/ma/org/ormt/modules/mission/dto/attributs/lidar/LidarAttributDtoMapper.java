package ma.org.ormt.modules.mission.dto.attributs.lidar;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.mission.models.LidarAttribut;

@Mapper
public interface LidarAttributDtoMapper extends BaseDtoMapper<LidarAttribut, LidarAttributDto> {

}