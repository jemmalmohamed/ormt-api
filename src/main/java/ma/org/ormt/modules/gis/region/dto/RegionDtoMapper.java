package ma.org.ormt.modules.gis.region.dto;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.gis.region.Region;

@Mapper
public interface RegionDtoMapper extends BaseDtoMapper<Region, RegionDto> {

}
