package ma.org.ormt.modules.espaces.controllers;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import ma.org.ormt.core.commun.base.controller.BaseController;
import ma.org.ormt.core.commun.rest.queries.QueryParams;
import ma.org.ormt.core.commun.rest.responses.RestResponse;
import ma.org.ormt.modules.espaces.dtos.EspaceDto;
import ma.org.ormt.modules.espaces.dtos.EspaceDtoMapper;
import ma.org.ormt.modules.espaces.dtos.details.EspaceDetailsDto;
import ma.org.ormt.modules.espaces.dtos.details.EspaceDetailsDtoMapper;
import ma.org.ormt.modules.espaces.models.Espace;
import ma.org.ormt.modules.espaces.services.EspaceService;
import ma.org.ormt.security.roleacces.services.RoleAccesService;

@RestController
@RequestMapping("api/v1/espaces")
@RequiredArgsConstructor
public class EspaceLoadController extends BaseController<Espace> {

        private static final String ENTITY_NAME = "espace";

        private final EspaceService espaceService;
        private final RoleAccesService roleAccesService;
        private final EspaceDtoMapper espaceDtoMapper;
        private final EspaceDetailsDtoMapper espaceDetailMapper;

        @Operation(summary = "Get all " + ENTITY_NAME + "s")
        @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Ok", content = {
                        @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = EspaceDto.class))) }),
                        @ApiResponse(responseCode = "404", description = ENTITY_NAME
                                        + " not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @GetMapping("")
        @PreAuthorize("hasAuthority('espace:list')")
        public ResponseEntity<RestResponse<List<EspaceDto>>> getEspaces(
                        @RequestParam(value = "pageIndex", defaultValue = "0") int pageIndex,
                        @RequestParam(value = "pageSize", defaultValue = "-1") int pageSize,
                        @RequestParam(value = "sortField", defaultValue = "createdDate") String sortField,
                        @RequestParam(value = "sortDirection", defaultValue = "DESC") Direction direction,
                        @RequestParam(value = "filters", defaultValue = "") List<String> filters,
                        @RequestParam(value = "globalFilter", defaultValue = "") String globalFilter) {

                // Create query params
                QueryParams requestParams = createQueryParams(pageIndex, pageSize, sortField, direction, filters,
                                globalFilter);

                // Check if user has admin role
                String currentUserRole = roleAccesService.getCurrentUserRole();
                Page<Espace> espacePage;

                if ("ROLE_ADMIN".equalsIgnoreCase(currentUserRole)) {
                        // For admin, get all entities without ID filtering
                        espacePage = espaceService.getEntityList(requestParams);
                } else {
                        // For non-admin users, get entities with restricted access
                        List<Long> accessibleEspaceIds = roleAccesService.getAccessibleResources(
                                        currentUserRole, "espace", "lecture");

                        espacePage = espaceService.getEntitiesByIds(accessibleEspaceIds, requestParams);
                }

                return buildResponseEntity(
                                espacePage.getContent(), EspaceDto.class,
                                adjustQueryParamsToGetAllRecords(requestParams, espacePage),
                                HttpStatus.OK);
        }

        @Operation(summary = "Get " + ENTITY_NAME + " by id")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ok", content = {
                                        @Content(mediaType = "application/json", schema = @Schema(implementation = EspaceDetailsDto.class)) }),
                        @ApiResponse(responseCode = "404", description = ENTITY_NAME
                                        + " not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @GetMapping("/{id}")
        @PreAuthorize("hasAuthority('espace:read')")
        public ResponseEntity<RestResponse<EspaceDetailsDto>> getEspace(@PathVariable("id") Long id) {
                if (!hasAccessToResource(id, "lecture")) {
                        return createForbiddenResponse();
                }

                try {

                        Espace espace = espaceService.findById(id)
                                        .orElseThrow(() -> new EntityNotFoundException(
                                                        "Espace with id " + id + " not found"));
                        return buildResponseEntity(espace, EspaceDetailsDto.class, HttpStatus.OK);
                } catch (EntityNotFoundException e) {
                        return createNotFoundResponse(e.getMessage());
                }
        }

        @Override
        protected <DTO> DTO mapToDto(Espace entity, Class<DTO> dtoClass) {
                if (dtoClass == EspaceDetailsDto.class) {
                        return dtoClass.cast(espaceDetailMapper.mapToDto(entity, roleAccesService));
                } else if (dtoClass == EspaceDto.class) {
                        return dtoClass.cast(espaceDtoMapper.mapToDto(entity, roleAccesService));
                }
                throw new IllegalArgumentException("Unsupported DTO type: " + dtoClass.getName());
        }

        /**
         * Check if current user has access to a specific resource
         * Admin role always has access to all resources
         */
        private boolean hasAccessToResource(Long resourceId, String permission) {
                String currentUserRole = roleAccesService.getCurrentUserRole();

                // Admin role has access to everything
                if ("ROLE_ADMIN".equalsIgnoreCase(currentUserRole)) {
                        return true;
                }

                List<Long> accessibleIds = roleAccesService.getAccessibleResources(
                                currentUserRole, "espace", permission);
                return accessibleIds != null && accessibleIds.contains(resourceId);
        }

        /**
         * Create a forbidden response with appropriate message
         */
        private <T> ResponseEntity<RestResponse<T>> createForbiddenResponse() {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body(new RestResponse<>(HttpStatus.FORBIDDEN, "Permission denied", false, null, null));
        }

        /**
         * Create a not found response with error message
         */
        private <T> ResponseEntity<RestResponse<T>> createNotFoundResponse(String message) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(new RestResponse<>(HttpStatus.NOT_FOUND, message, false, null, null));
        }
}