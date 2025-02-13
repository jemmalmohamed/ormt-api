package ma.org.ormt.modules.gis.basemap.dto;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.gis.basemap.Basemap;

@Mapper
public interface BasemapDtoMapper extends BaseDtoMapper<Basemap, BasemapDto> {

}
