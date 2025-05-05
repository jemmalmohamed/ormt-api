package ma.org.ormt.modules.users.roleacces.dtos.request;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.users.roleacces.models.RoleAcces;

@Mapper
public interface RoleAccesRequestDtoMapper extends BaseDtoMapper<RoleAcces, RoleAccesRequestDto> {

}
