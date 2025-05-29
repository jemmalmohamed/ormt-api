package ma.org.ormt.security.roles.dto.detail;

import org.keycloak.representations.idm.RoleRepresentation;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;

@Mapper()
public interface RoleDetailsDtoMapper extends BaseDtoMapper<RoleRepresentation, RoleDetailsDto> {

    @AfterMapping
    default void afterMapping(RoleRepresentation rol, @MappingTarget RoleDetailsDto dto) {
        // List<String> roles = rol.getClientRoles().get("ormt-api");
        // dto.setRoles(roles);
    }
}