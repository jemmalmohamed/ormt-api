package ma.org.ormt.modules.roleacces.dtos;

import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.roleacces.models.RoleAcces;

@Mapper
public interface RoleAccesDtoMapper extends BaseDtoMapper<RoleAcces, RoleAccesDto> {

}
