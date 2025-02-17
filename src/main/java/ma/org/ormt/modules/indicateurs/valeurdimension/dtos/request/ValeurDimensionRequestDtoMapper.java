package ma.org.ormt.modules.indicateurs.valeurdimension.dtos.request;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.indicateurs.valeurdimension.models.ValeurDimension;

@Mapper
public interface ValeurDimensionRequestDtoMapper extends BaseDtoMapper<ValeurDimension, ValeurDimensionRequestDto> {
}