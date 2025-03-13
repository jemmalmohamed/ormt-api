package ma.org.ormt.modules.indicateurs.source.dtos.summary;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.indicateurs.source.models.Source;

@Mapper
public interface SourceSummaryDtoMapper extends BaseDtoMapper<Source, SourceSummaryDto> {

}