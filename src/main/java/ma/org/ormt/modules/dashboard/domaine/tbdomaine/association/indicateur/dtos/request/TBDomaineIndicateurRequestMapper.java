package ma.org.ormt.modules.dashboard.domaine.tbdomaine.association.indicateur.dtos.request;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.association.indicateur.TBDomaineIndicateur;

@Mapper
public interface TBDomaineIndicateurRequestMapper
                extends BaseDtoMapper<TBDomaineIndicateur, TBDomaineIndicateurRequestDto> {

}
