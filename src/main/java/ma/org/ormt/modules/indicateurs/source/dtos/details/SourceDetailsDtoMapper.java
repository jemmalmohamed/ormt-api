package ma.org.ormt.modules.indicateurs.source.dtos.details;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.indicateurs.source.models.Source;

@Mapper
public interface SourceDetailsDtoMapper extends BaseDtoMapper<Source, SourceDetailsDto> {
}