package ma.org.ormt.modules.indicateurs.indicateur.dtos.summary;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;

@Mapper
public interface IndicateurSummaryDtoMapper extends BaseDtoMapper<Indicateur, IndicateurSummaryDto> {

}