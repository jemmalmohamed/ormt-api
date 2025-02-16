package ma.org.ormt.modules.dimension.dtos.summary;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.dimension.models.Dimension;

@Mapper
public interface DimensionSummaryDtoMapper extends BaseDtoMapper<Dimension, DimensionSummaryDto> {
}