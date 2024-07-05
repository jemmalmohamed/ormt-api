package ma.org.ancfcc.pva.modules.avion.dto;

import org.mapstruct.Mapper;

import ma.org.ancfcc.pva.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ancfcc.pva.modules.avion.Avion;

@Mapper
public interface AvionDtoMapper extends BaseDtoMapper<Avion, AvionDto> {

}
