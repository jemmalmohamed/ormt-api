package ma.org.ormt.modules.indicateurs.indicateur.association.dimension.dtos;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.indicateurs.indicateur.association.dimension.models.IndicateurDimension;

@Mapper()
public interface IndicateurDimensionDtoMapper extends BaseDtoMapper<IndicateurDimension, IndicateurDimensionDto> {

}