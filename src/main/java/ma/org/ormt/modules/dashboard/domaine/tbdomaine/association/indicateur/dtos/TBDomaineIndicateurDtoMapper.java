package ma.org.ormt.modules.dashboard.domaine.tbdomaine.association.indicateur.dtos;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.association.indicateur.TBDomaineIndicateur;

@Mapper
public interface TBDomaineIndicateurDtoMapper extends BaseDtoMapper<TBDomaineIndicateur, TBDomaineIndicateurDto> {
}