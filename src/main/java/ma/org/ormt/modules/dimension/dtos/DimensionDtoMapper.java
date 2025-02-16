package ma.org.ormt.modules.dimension.dtos;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.dimension.models.Dimension;

@Mapper
public interface DimensionDtoMapper extends BaseDtoMapper<Dimension, DimensionDto> {
}