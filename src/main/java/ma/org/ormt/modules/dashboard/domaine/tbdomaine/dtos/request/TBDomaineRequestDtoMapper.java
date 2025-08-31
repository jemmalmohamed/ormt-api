package ma.org.ormt.modules.dashboard.domaine.tbdomaine.dtos.request;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.models.TBDomaine;

@Mapper
public interface TBDomaineRequestDtoMapper extends BaseDtoMapper<TBDomaine, TBDomaineRequestDto> {

}
