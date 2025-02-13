package ma.org.ormt.modules.gis.basemap.dto.request;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.gis.basemap.Basemap;

@Mapper
public interface BasemapRequestMapper extends BaseDtoMapper<Basemap, BasemapRequestDto> {

}
