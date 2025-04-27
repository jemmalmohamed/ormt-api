package ma.org.ormt.security.roleacces.dtos.request;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.security.roleacces.models.RoleAcces;

@Mapper
public interface RoleAccesRequestDtoMapper extends BaseDtoMapper<RoleAcces, RoleAccesRequestDto> {

}
