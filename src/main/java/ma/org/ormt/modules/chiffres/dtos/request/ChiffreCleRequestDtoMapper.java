package ma.org.ormt.modules.chiffres.dtos.request;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.chiffres.models.ChiffreCle;

@Mapper
public interface ChiffreCleRequestDtoMapper extends BaseDtoMapper<ChiffreCle, ChiffreCleRequestDto> {
}