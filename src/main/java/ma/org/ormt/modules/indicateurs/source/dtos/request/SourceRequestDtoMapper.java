package ma.org.ormt.modules.indicateurs.source.dtos.request;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.indicateurs.source.models.Source;

@Mapper
public interface SourceRequestDtoMapper extends BaseDtoMapper<Source, SourceRequestDto> {
}