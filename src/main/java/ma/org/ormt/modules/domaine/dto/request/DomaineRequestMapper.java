package ma.org.ormt.modules.domaine.dto.request;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.domaine.Domaine;

@Mapper
public interface DomaineRequestMapper extends BaseDtoMapper<Domaine, DomaineRequestDto> {

}
