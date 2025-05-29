package ma.org.ormt.security.users.users.dtos;

import java.util.List;

import org.keycloak.representations.idm.UserRepresentation;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;

@Mapper()
public interface UserDtoMapper extends BaseDtoMapper<UserRepresentation, UserDto> {

    @AfterMapping
    default void afterMapping(UserRepresentation user, @MappingTarget UserDto dto) {
        List<String> roles = user.getClientRoles().get("ormt-api");
        dto.setRoles(roles);
    }
}