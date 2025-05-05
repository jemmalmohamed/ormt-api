package ma.org.ormt.modules.users.users.dtos.request;

import org.keycloak.representations.account.UserRepresentation;
import org.mapstruct.Mapper;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;

@Mapper
public interface UserRequestDtoMapper extends BaseDtoMapper<UserRepresentation, UserRequestDto> {
}