package ma.org.ormt.modules.chiffres.controllers;

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
import ma.org.ormt.modules.chiffres.dtos.ChiffreCleDto;
import ma.org.ormt.modules.chiffres.dtos.ChiffreCleDtoMapper;
import ma.org.ormt.modules.chiffres.dtos.details.ChiffreCleDetailsDto;
import ma.org.ormt.modules.chiffres.dtos.details.ChiffreCleDetailsDtoMapper;
import ma.org.ormt.modules.chiffres.models.ChiffreCle;
import ma.org.ormt.modules.chiffres.services.ChiffreCleService;
import ma.org.ormt.modules.roleacces.services.RoleAccesService;

@RestController
@RequestMapping("api/v1/chiffrecles")
@RequiredArgsConstructor
public class ChiffreCleLoadController extends BaseController<ChiffreCle> {

        private static final String ENTITY_NAME = "chiffrecle";

        private final ChiffreCleService chiffrecleService;
        private final RoleAccesService roleAccesService;
        private final ChiffreCleDtoMapper chiffrecleDtoMapper;
        private final ChiffreCleDetailsDtoMapper chiffrecleDetailMapper;

        @Operation(summary = "Get all " + ENTITY_NAME + "s")
        @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Ok", content = {
                        @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ChiffreCleDto.class))) }),
                        @ApiResponse(responseCode = "404", description = ENTITY_NAME
                                        + " not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @GetMapping("")
        @PreAuthorize("hasAuthority('chiffrecle:list')")
        public ResponseEntity<RestResponse<List<ChiffreCleDto>>> getChiffreCles(
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
                Page<ChiffreCle> chiffreclePage;

                if ("ROLE_ADMIN".equalsIgnoreCase(currentUserRole)) {
                        // For admin, get all entities without ID filtering
                        chiffreclePage = chiffrecleService.getEntityList(requestParams);
                } else {
                        // For non-admin users, get entities with restricted access
                        List<Long> accessibleChiffreCleIds = roleAccesService.getAccessibleResources(
                                        currentUserRole, "chiffrecle", "lecture");

                        chiffreclePage = chiffrecleService.getEntitiesByIds(accessibleChiffreCleIds, requestParams);
                }

                return buildResponseEntity(
                                chiffreclePage.getContent(), ChiffreCleDto.class,
                                adjustQueryParamsToGetAllRecords(requestParams, chiffreclePage),
                                HttpStatus.OK);
        }

        @Operation(summary = "Get " + ENTITY_NAME + " by id")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ok", content = {
                                        @Content(mediaType = "application/json", schema = @Schema(implementation = ChiffreCleDetailsDto.class)) }),
                        @ApiResponse(responseCode = "404", description = ENTITY_NAME
                                        + " not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @GetMapping("/{id}")
        @PreAuthorize("hasAuthority('chiffrecle:read')")
        public ResponseEntity<RestResponse<ChiffreCleDetailsDto>> getChiffreCle(@PathVariable("id") Long id) {
                if (!hasAccessToResource(id, "lecture")) {
                        return createForbiddenResponse();
                }

                try {

                        ChiffreCle chiffrecle = chiffrecleService.findById(id)
                                        .orElseThrow(() -> new EntityNotFoundException(
                                                        "ChiffreCle with id " + id + " not found"));
                        return buildResponseEntity(chiffrecle, ChiffreCleDetailsDto.class, HttpStatus.OK);
                } catch (EntityNotFoundException e) {
                        return createNotFoundResponse(e.getMessage());
                }
        }

        @Override
        protected <DTO> DTO mapToDto(ChiffreCle entity, Class<DTO> dtoClass) {
                if (dtoClass == ChiffreCleDetailsDto.class) {
                        return dtoClass.cast(chiffrecleDetailMapper.mapToDto(entity, roleAccesService));
                } else if (dtoClass == ChiffreCleDto.class) {
                        return dtoClass.cast(chiffrecleDtoMapper.mapToDto(entity, roleAccesService));
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
                                currentUserRole, "chiffrecle", permission);
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