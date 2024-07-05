package ma.org.ormt.modules.avion.dto;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.avion.Avion;

@Mapper
public interface AvionDtoMapper extends BaseDtoMapper<Avion, AvionDto> {

}
