package ma.org.ormt.modules.domaine.dtos.request;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.domaine.models.Domaine;

@Mapper
public interface DomaineRequestDtoMapper extends BaseDtoMapper<Domaine, DomaineRequestDto> {

}
