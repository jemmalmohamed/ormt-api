package ma.org.ormt.modules.chiffres.dtos.details;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.chiffres.models.ChiffreCle;

@Mapper
public interface ChiffreCleDetailsDtoMapper extends BaseDtoMapper<ChiffreCle, ChiffreCleDetailsDto> {

}