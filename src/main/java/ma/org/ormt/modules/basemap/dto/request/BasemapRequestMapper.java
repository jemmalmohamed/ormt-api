package ma.org.ormt.modules.basemap.dto.request;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.basemap.Basemap;

@Mapper
public interface BasemapRequestMapper extends BaseDtoMapper<Basemap, BasemapRequestDto> {

}
