package ma.org.ormt.modules.indicateurs.indicateur.dtos.detail;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;

@Mapper
public interface IndicateurDetailDtoMapper extends BaseDtoMapper<Indicateur, IndicateurDetailDto> {

}