package ma.org.ormt.modules.espaces.dtos.details;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.espaces.models.Espace;

@Mapper
public interface EspaceDetailsDtoMapper extends BaseDtoMapper<Espace, EspaceDetailsDto> {
}