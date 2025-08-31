package ma.org.ormt.modules.dashboard.tableaubord.dtos.details;

import java.util.List;

import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.dashboard.tableaubord.models.TableauBord;
import ma.org.ormt.modules.roleacces.dtos.summary.RoleAccesSummaryDto;
import ma.org.ormt.modules.roleacces.services.RoleAccesService;
import ma.org.ormt.modules.roleacces.utils.RoleAccesMappingUtil;

@Mapper
public interface TableauBordDetailsDtoMapper extends BaseDtoMapper<TableauBord, TableauBordDetailsDto> {

    @AfterMapping
    default void afterMapping(TableauBord tableauBord, @MappingTarget TableauBordDetailsDto dto,
            @Context Object... services) {

        RoleAccesService roleAccesService = findService(services,
                RoleAccesService.class);

        if (roleAccesService == null) {
            return;
        }

        List<RoleAccesSummaryDto> roleAccesDtos = RoleAccesMappingUtil
                .mapForRessource(roleAccesService, "tableauBord", tableauBord.getId());
        dto.setRoleAcces(roleAccesDtos);

    }
}