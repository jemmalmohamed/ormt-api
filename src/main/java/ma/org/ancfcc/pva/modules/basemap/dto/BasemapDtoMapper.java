package ma.org.ancfcc.pva.modules.basemap.dto;

import org.mapstruct.Mapper;

import ma.org.ancfcc.pva.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ancfcc.pva.modules.basemap.Basemap;

@Mapper
public interface BasemapDtoMapper extends BaseDtoMapper<Basemap, BasemapDto> {

}
