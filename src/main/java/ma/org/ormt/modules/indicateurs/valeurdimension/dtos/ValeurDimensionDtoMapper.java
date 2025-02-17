package ma.org.ormt.modules.indicateurs.valeurdimension.dtos;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.indicateurs.valeurdimension.models.ValeurDimension;

@Mapper
public interface ValeurDimensionDtoMapper extends BaseDtoMapper<ValeurDimension, ValeurDimensionDto> {

}