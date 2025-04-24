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

                // Get the current user's role and retrieve accessible resources based on that
                // role
                String currentUserRole = roleAccesService.getCurrentUserRole();
                List<Long> accessibleEspaceIds = roleAccesService.getAccessibleResources(
                                currentUserRole, "espace", "lecture");

                QueryParams requestParams = createQueryParams(pageIndex, pageSize, sortField, direction, filters,
                                globalFilter);

                Page<Espace> espacePage = espaceService.getEntitiesByIds(accessibleEspaceIds, requestParams);

                List<EspaceDto> dtos = espaceDtoMapper.mapToDto(espacePage.getContent());

                QueryParams queryParams = adjustQueryParamsForAllRecords(requestParams, espacePage);

                return buildResponseEntity(dtos, queryParams, HttpStatus.OK);
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
                // Get current user's role and accessible resources
                String currentUserRole = roleAccesService.getCurrentUserRole();
                List<Long> accessibleEspaceIds = roleAccesService.getAccessibleResources(
                                currentUserRole, "espace", "lecture");
                
                // Check permissions
                if (!accessibleEspaceIds.contains(id)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                        .body(new RestResponse<EspaceDetailsDto>(HttpStatus.FORBIDDEN, "Permission denied", false, null, null));
                }
                
                try {
                        // Fetch the espace
                        Espace espace = espaceService.findById(id)
                                .orElseThrow(() -> new EntityNotFoundException("Espace with id " + id + " not found"));
                        
                        // Map and return the entity
                        return buildResponseEntity(espace, EspaceDetailsDto.class, HttpStatus.OK);
                } catch (EntityNotFoundException e) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(new RestResponse<EspaceDetailsDto>(HttpStatus.NOT_FOUND, e.getMessage(), false, null, null));
                }
        }

        @Override
        protected <DTO> DTO mapToDto(Espace entity, Class<DTO> dtoClass) {
                if (dtoClass == EspaceDetailsDto.class) {
                        return dtoClass.cast(espaceDetailMapper.mapToDto(entity));
                } else if (dtoClass == EspaceDto.class) {
                        return dtoClass.cast(espaceDtoMapper.mapToDto(entity));
                }
                throw new IllegalArgumentException("Unsupported DTO type: " + dtoClass.getName());
        }
}