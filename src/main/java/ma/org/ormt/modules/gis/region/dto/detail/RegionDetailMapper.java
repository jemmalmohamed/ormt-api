package ma.org.ormt.modules.gis.region.dto.detail;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.gis.region.Region;

@Mapper
public interface RegionDetailMapper extends BaseDtoMapper<Region, RegionDetailDto> {

}
