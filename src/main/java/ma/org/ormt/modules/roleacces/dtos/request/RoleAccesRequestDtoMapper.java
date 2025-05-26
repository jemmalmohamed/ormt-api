package ma.org.ormt.modules.roleacces.dtos.request;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.roleacces.models.RoleAcces;

@Mapper
public interface RoleAccesRequestDtoMapper extends BaseDtoMapper<RoleAcces, RoleAccesRequestDto> {

}
