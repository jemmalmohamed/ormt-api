package ma.org.ormt.security.users.users.dtos;

import org.keycloak.representations.idm.UserRepresentation;
import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;

// Add 'uses' property to specify nested mappers
@Mapper()
public interface UserDtoMapper extends BaseDtoMapper<UserRepresentation, UserDto> {

}