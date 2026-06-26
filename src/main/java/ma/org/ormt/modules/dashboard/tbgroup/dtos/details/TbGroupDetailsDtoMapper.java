package ma.org.ormt.modules.dashboard.tbgroup.dtos.details;

import java.util.List;

import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.dashboard.tbgroup.models.TbGroup;
import ma.org.ormt.modules.roleacces.dtos.summary.RoleAccesSummaryDto;
import ma.org.ormt.modules.roleacces.services.RoleAccesService;
import ma.org.ormt.modules.roleacces.utils.RoleAccesMappingUtil;

@Mapper
public interface TbGroupDetailsDtoMapper extends BaseDtoMapper<TbGroup, TbGroupDetailsDto> {

    @AfterMapping
    default void afterMapping(TbGroup tbGroup, @MappingTarget TbGroupDetailsDto dto,
            @Context Object... services) {

        RoleAccesService roleAccesService = findService(services,
                RoleAccesService.class);

        if (roleAccesService == null) {
            return;
        }

        List<RoleAccesSummaryDto> roleAccesDtos = RoleAccesMappingUtil
                .mapForRessource(roleAccesService, "tbGroup", tbGroup.getId());
        dto.setRoleAcces(roleAccesDtos);

    }
}