package ma.org.ormt.modules.domaines.sousdomaine.dtos.publicdto;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.domaines.sousdomaine.models.SousDomaine;
import ma.org.ormt.modules.indicateurs.indicateur.dtos.detail.IndicateurDetailDtoMapper;

@Mapper(uses = { IndicateurDetailDtoMapper.class })
public interface SousDomainePublicDtoMapper extends BaseDtoMapper<SousDomaine, SousDomainePublicDto> {

}