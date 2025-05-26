package ma.org.ormt.security.users.roleacces.dtos.summary;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.security.users.roleacces.models.RoleAcces;

@Mapper
public interface RoleAccesSummaryDtoMapper extends BaseDtoMapper<RoleAcces, RoleAccesSummaryDto> {

}
