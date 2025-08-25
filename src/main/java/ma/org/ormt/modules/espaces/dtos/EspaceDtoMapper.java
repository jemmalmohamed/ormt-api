package ma.org.ormt.modules.espaces.dtos;

import java.util.List;

import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.espaces.models.Espace;
import ma.org.ormt.modules.roleacces.dtos.summary.RoleAccesSummaryDto;
import ma.org.ormt.modules.roleacces.services.RoleAccesService;
import ma.org.ormt.modules.roleacces.utils.RoleAccesMappingUtil;

// Add 'uses' property to specify nested mappers
@Mapper()
public interface EspaceDtoMapper extends BaseDtoMapper<Espace, EspaceDto> {

    @AfterMapping
    default void afterMapping(Espace espace, @MappingTarget EspaceDto dto, @Context Object... services) {

        RoleAccesService roleAccesService = findService(services,
                RoleAccesService.class);

        if (roleAccesService == null) {
            return;
        }

        List<RoleAccesSummaryDto> roleAccesDtos = RoleAccesMappingUtil
                .mapForRessource(roleAccesService, "espace", espace.getId());
        dto.setRoleAcces(roleAccesDtos);

    }
}