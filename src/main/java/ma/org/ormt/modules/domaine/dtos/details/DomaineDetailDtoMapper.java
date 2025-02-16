package ma.org.ormt.modules.domaine.dtos.details;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.domaine.models.Domaine;

@Mapper
public interface DomaineDetailDtoMapper extends BaseDtoMapper<Domaine, DomaineDetailDto> {

}
