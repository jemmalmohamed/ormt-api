package ma.org.ormt.modules.indicateur.dto;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.indicateur.models.Indicateur;

@Mapper
public interface IndicateurDtoMapper extends BaseDtoMapper<Indicateur, IndicateurDto> {

}