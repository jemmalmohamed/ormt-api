package ma.org.ormt.modules.domaines.sousdomaine.dtos;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.domaines.sousdomaine.models.SousDomaine;
import ma.org.ormt.modules.indicateurs.indicateur.dtos.series.IndicateurChartDtoMapper;

@Mapper(uses = { IndicateurChartDtoMapper.class })
public interface SousDomaineDtoMapper extends BaseDtoMapper<SousDomaine, SousDomaineDto> {

}
