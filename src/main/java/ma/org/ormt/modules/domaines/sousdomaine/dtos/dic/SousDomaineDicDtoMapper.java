package ma.org.ormt.modules.domaines.sousdomaine.dtos.dic;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.domaines.sousdomaine.models.SousDomaine;
import ma.org.ormt.modules.indicateurs.indicateur.dtos.series.IndicateurChartDtoMapper;

/**
 * Mapper interface for converting between SousDomaine and SousDomaineDicDto.
 */
@Mapper()
public interface SousDomaineDicDtoMapper extends BaseDtoMapper<SousDomaine, SousDomaineDicDto> {

}
