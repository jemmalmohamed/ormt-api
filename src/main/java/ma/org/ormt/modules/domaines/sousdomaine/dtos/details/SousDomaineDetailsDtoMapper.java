package ma.org.ormt.modules.domaines.sousdomaine.dtos.details;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.domaines.sousdomaine.models.SousDomaine;
import ma.org.ormt.modules.indicateurs.indicateur.dtos.sousdomaine.IndicateurSousDomaineDetailDtoMapper;

@Mapper(uses = {IndicateurSousDomaineDetailDtoMapper.class})
public interface SousDomaineDetailsDtoMapper extends BaseDtoMapper<SousDomaine, SousDomaineDetailsDto> {

}
