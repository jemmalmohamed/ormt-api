package ma.org.ormt.modules.dashboard.domaine.tbdomaine.dtos;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.models.TBDomaine;

@Mapper
public interface TBDomaineDtoMapper extends BaseDtoMapper<TBDomaine, TBDomaineDto> {

}
