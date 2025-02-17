package ma.org.ormt.modules.domaines.domaine.dtos.details;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.domaines.domaine.models.Domaine;

@Mapper
public interface DomaineDetailDtoMapper extends BaseDtoMapper<Domaine, DomaineDetailDto> {

}
