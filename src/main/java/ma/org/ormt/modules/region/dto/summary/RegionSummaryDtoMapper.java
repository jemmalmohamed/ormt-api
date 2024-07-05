package ma.org.ormt.modules.region.dto.summary;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.region.Region;

@Mapper
public interface RegionSummaryDtoMapper extends BaseDtoMapper<Region, RegionSummaryDto> {

}
