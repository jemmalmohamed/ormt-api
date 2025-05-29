package ma.org.ormt.security.roles.dto;

import org.keycloak.representations.idm.RoleRepresentation;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;

@Mapper()
public interface RoleDtoMapper extends BaseDtoMapper<RoleRepresentation, RoleDto> {

    @AfterMapping
    default void afterMapping(RoleRepresentation rol, @MappingTarget RoleDto dto) {
        // List<String> roles = rol.getClientRoles().get("ormt-api");
        // dto.setRoles(roles);
    }
}