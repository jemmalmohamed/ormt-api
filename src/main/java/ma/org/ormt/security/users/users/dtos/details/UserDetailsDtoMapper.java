package ma.org.ormt.security.users.users.dtos.details;

import org.keycloak.representations.idm.UserRepresentation;
import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;

@Mapper
public interface UserDetailsDtoMapper extends BaseDtoMapper<UserRepresentation, UserDetailsDto> {

}