package ma.org.ormt.modules.indicateurs.donnee.dtos.request;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.indicateurs.donnee.models.DonneeIndicateur;

@Mapper
public interface DonneeIndicateurRequestDtoMapper extends BaseDtoMapper<DonneeIndicateur, DonneeIndicateurRequestDto> {
}