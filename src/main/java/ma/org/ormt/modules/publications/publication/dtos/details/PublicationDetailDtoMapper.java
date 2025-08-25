package ma.org.ormt.modules.publications.publication.dtos.details;

import java.util.List;

import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import ma.org.ormt.core.commun.base.mapper.BaseDtoMapper;
import ma.org.ormt.modules.publications.publication.dtos.PublicationDto;
import ma.org.ormt.modules.publications.publication.models.Publication;
import ma.org.ormt.modules.roleacces.dtos.summary.RoleAccesSummaryDto;
import ma.org.ormt.modules.roleacces.services.RoleAccesService;
import ma.org.ormt.modules.roleacces.utils.RoleAccesMappingUtil;

@Mapper
public interface PublicationDetailDtoMapper extends BaseDtoMapper<Publication, PublicationDetailDto> {

    @AfterMapping
    default void afterMapping(Publication publication, @MappingTarget PublicationDto dto, @Context Object... services) {

        RoleAccesService roleAccesService = findService(services,
                RoleAccesService.class);

        if (roleAccesService == null) {
            return;
        }

        List<RoleAccesSummaryDto> roleAccesDtos = RoleAccesMappingUtil
                .mapForRessource(roleAccesService, "publication", publication.getId());
        dto.setRoleAcces(roleAccesDtos);

    }
}
