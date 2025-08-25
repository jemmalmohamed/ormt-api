package ma.org.ormt.modules.roleacces.utils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import ma.org.ormt.modules.roleacces.dtos.summary.RoleAccesSummaryDto;
import ma.org.ormt.modules.roleacces.models.RoleAcces;
import ma.org.ormt.modules.roleacces.services.RoleAccesService;

/**
 * Utility methods to help mappers attach role access summaries.
 */
public final class RoleAccesMappingUtil {

    private RoleAccesMappingUtil() {
    }

    public static List<RoleAccesSummaryDto> mapForRessource(RoleAccesService service, String ressource,
            Long ressourceId) {
        if (service == null || ressource == null || ressourceId == null) {
            return Collections.emptyList();
        }

        List<RoleAcces> rolesAccesList = service.getAccesByRessource(ressource, ressourceId);
        if (rolesAccesList == null || rolesAccesList.isEmpty()) {
            return Collections.emptyList();
        }

        return rolesAccesList.stream()
                .filter(Objects::nonNull)
                .map(RoleAccesMappingUtil::toSummary)
                .collect(Collectors.toList());
    }

    public static RoleAccesSummaryDto toSummary(RoleAcces roleAcces) {
        RoleAccesSummaryDto dto = new RoleAccesSummaryDto();
        dto.setId(roleAcces.getId());
        dto.setRoleCode(roleAcces.getRoleCode());
        dto.setNiveauAcces(roleAcces.getNiveauAcces());
        return dto;
    }

    /**
     * Apply role accesses for a given resource type and id, generically.
     *
     * @param service              role access service to query and persist
     * @param roleAccesList        list of items holding role access data
     * @param resourceType         resource type key (e.g., "espace")
     * @param resourceId           id of the resource
     * @param roleCodeExtractor    extractor of role code from list item
     * @param niveauAccesExtractor extractor of access level from list item
     * @param createdBy            audit createdBy value
     * @param <T>                  item type
     */
    public static <T> void applyRoleAccesses(
            RoleAccesService service,
            List<T> roleAccesList,
            String resourceType,
            Long resourceId,
            Function<T, String> roleCodeExtractor,
            Function<T, String> niveauAccesExtractor,
            String createdBy) {
        if (service == null || roleAccesList == null || roleAccesList.isEmpty()) {
            return;
        }

        roleAccesList.forEach(item -> {
            String roleCode = roleCodeExtractor.apply(item);
            String niveauAcces = niveauAccesExtractor.apply(item);

            if (!service.hasAccess(roleCode, resourceType, resourceId, niveauAcces)) {
                service.addAccess(roleCode, resourceType, resourceId, niveauAcces, createdBy);
            }
        });
    }
}
