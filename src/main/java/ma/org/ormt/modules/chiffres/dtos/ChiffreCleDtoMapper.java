package ma.org.ormt.modules.chiffres.dtos;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.chiffres.models.ChiffreCle;
import ma.org.ormt.modules.roleacces.dtos.summary.RoleAccesSummaryDto;
import ma.org.ormt.modules.roleacces.models.RoleAcces;
import ma.org.ormt.modules.roleacces.services.RoleAccesService;

// Add 'uses' property to specify nested mappers
@Mapper()
public interface ChiffreCleDtoMapper extends BaseDtoMapper<ChiffreCle, ChiffreCleDto> {

    @AfterMapping
    default void afterMapping(ChiffreCle chiffreCle, @MappingTarget ChiffreCleDto dto, @Context Object... services) {

        RoleAccesService roleAccesService = findService(services,
                RoleAccesService.class);

        if (roleAccesService == null) {
            return;
        }

        List<RoleAcces> rolesAccesList = roleAccesService.getAccesByRessource("chiffreCle", chiffreCle.getId());

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