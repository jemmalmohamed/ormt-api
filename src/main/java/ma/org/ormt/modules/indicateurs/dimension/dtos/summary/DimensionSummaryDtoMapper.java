package ma.org.ormt.modules.indicateurs.dimension.dtos.summary;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.indicateurs.dimension.models.Dimension;

@Mapper
public interface DimensionSummaryDtoMapper extends BaseDtoMapper<Dimension, DimensionSummaryDto> {

}