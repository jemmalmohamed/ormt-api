package ma.org.ormt.modules.espaces.dtos.summary;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.espaces.models.Espace;

@Mapper
public interface EspaceSummaryDtoMapper extends BaseDtoMapper<Espace, EspaceSummaryDto> {

}