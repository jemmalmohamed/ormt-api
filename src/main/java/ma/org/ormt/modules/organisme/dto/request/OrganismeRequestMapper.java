package ma.org.ormt.modules.organisme.dto.request;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.organisme.Organisme;

@Mapper
public interface OrganismeRequestMapper extends BaseDtoMapper<Organisme, OrganismeRequestDto> {

}
