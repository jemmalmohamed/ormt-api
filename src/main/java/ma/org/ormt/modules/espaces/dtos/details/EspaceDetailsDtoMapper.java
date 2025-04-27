package ma.org.ormt.modules.espaces.dtos.details;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.espaces.dtos.EspaceDto;
import ma.org.ormt.modules.espaces.models.Espace;
import ma.org.ormt.security.roleacces.dtos.summary.RoleAccesSummaryDto;
import ma.org.ormt.security.roleacces.models.RoleAcces;
import ma.org.ormt.security.roleacces.services.RoleAccesService;

@Mapper
public interface EspaceDetailsDtoMapper extends BaseDtoMapper<Espace, EspaceDetailsDto> {

    @AfterMapping
    default void afterMapping(Espace espace, @MappingTarget EspaceDto dto, @Context Object... services) {

        RoleAccesService roleAccesService = findService(services,
                RoleAccesService.class);

        if (roleAccesService == null) {
            return;
        }

        List<RoleAcces> rolesAccesList = roleAccesService.getAccesByRessource("espace", espace.getId());

        List<RoleAccesSummaryDto> roleAccesDtos = rolesAccesList.stream()
                .map(roleAcces -> {
                    RoleAccesSummaryDto roleAccesDto = new RoleAccesSummaryDto();
                    roleAccesDto.setId(roleAcces.getId());
                    roleAccesDto.setRoleCode(roleAcces.getRoleCode());
                    roleAccesDto.setNiveauAcces(roleAcces.getNiveauAcces());
                    return roleAccesDto;
                })
                .collect(Collectors.toList());

        dto.setRoleAcces(roleAccesDtos);

    }
}