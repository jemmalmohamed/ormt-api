package ma.org.ormt.security.users.roleacces.dtos.details;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.security.users.roleacces.models.RoleAcces;

@Mapper
public interface RoleAccesDetailDtoMapper extends BaseDtoMapper<RoleAcces, RoleAccesDetailDto> {

}
