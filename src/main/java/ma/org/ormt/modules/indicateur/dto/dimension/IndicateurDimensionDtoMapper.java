package ma.org.ormt.modules.indicateur.dto.dimension;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.indicateur.models.IndicateurDimension;

@Mapper
public interface IndicateurDimensionDtoMapper extends BaseDtoMapper<IndicateurDimension, IndicateurDimensionDto> {

}