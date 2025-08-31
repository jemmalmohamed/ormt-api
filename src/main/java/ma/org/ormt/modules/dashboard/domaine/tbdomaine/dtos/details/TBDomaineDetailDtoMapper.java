package ma.org.ormt.modules.dashboard.domaine.tbdomaine.dtos.details;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.models.TBDomaine;

@Mapper
public interface TBDomaineDetailDtoMapper extends BaseDtoMapper<TBDomaine, TBDomaineDetailDto> {

}
