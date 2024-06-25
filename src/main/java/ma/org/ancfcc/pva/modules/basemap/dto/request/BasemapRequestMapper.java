package ma.org.ancfcc.pva.modules.basemap.dto.request;

import org.mapstruct.Mapper;

import ma.org.ancfcc.pva.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ancfcc.pva.modules.basemap.Basemap;

@Mapper
public interface BasemapRequestMapper extends BaseDtoMapper<Basemap, BasemapRequestDto> {

}
