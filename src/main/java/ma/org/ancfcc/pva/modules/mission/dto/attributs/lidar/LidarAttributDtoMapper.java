package ma.org.ancfcc.pva.modules.mission.dto.attributs.lidar;

import org.mapstruct.Mapper;

import ma.org.ancfcc.pva.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ancfcc.pva.modules.mission.models.LidarAttribut;

@Mapper
public interface LidarAttributDtoMapper extends BaseDtoMapper<LidarAttribut, LidarAttributDto> {

}