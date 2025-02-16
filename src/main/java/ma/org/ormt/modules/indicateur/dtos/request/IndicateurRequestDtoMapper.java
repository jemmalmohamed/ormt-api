package ma.org.ormt.modules.indicateur.dtos.request;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.indicateur.models.Indicateur;

@Mapper
public interface IndicateurRequestDtoMapper extends BaseDtoMapper<Indicateur, IndicateurRequestDto> {
}