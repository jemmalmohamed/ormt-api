package ma.org.ormt.modules.espaces.controllers.admin;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.org.ormt.core.commun.base.controller.BaseController;
import ma.org.ormt.core.commun.rest.queries.QueryParams;
import ma.org.ormt.core.commun.rest.responses.RestResponse;
import ma.org.ormt.modules.espaces.dtos.EspaceDto;
import ma.org.ormt.modules.espaces.dtos.EspaceDtoMapper;
import ma.org.ormt.modules.espaces.dtos.details.EspaceDetailsDto;
import ma.org.ormt.modules.espaces.dtos.details.EspaceDetailsDtoMapper;
import ma.org.ormt.modules.espaces.models.Espace;
import ma.org.ormt.modules.espaces.services.EspaceService;

@Slf4j
@Validated
@RestController
@RequestMapping("api/v1/admin/espaces")
@RequiredArgsConstructor
public class EspaceAdminLoadController extends BaseController<Espace> {

        private static final String ENTITY_NAME = "espace";

        private final EspaceService espaceService;
        private final EspaceDtoMapper espaceDtoMapper;
        private final EspaceDetailsDtoMapper espaceDetailMapper;

        /**
         * Get all espaces with optional pagination, sorting, and filtering (Admin).
         */
        @Operation(summary = "Get all " + ENTITY_NAME + "s (Admin)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ok", content = {
                                        @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = EspaceDto.class))) }),
                        @ApiResponse(responseCode = "404", description = ENTITY_NAME
                                        + " not found", content = @Content(mediaType = "application/json")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "application/json"))
        })
        @GetMapping
        @PreAuthorize("hasAuthority('espace:list')")
        public ResponseEntity<RestResponse<List<EspaceDto>>> getEspaces(
                        @Parameter(description = "Page index (0-based)") @RequestParam(value = "pageIndex", defaultValue = "0") final int pageIndex,
                        @Parameter(description = "Page size (-1 for all)") @RequestParam(value = "pageSize", defaultValue = "-1") final int pageSize,
                        @Parameter(description = "Sort field") @RequestParam(value = "sortField", defaultValue = "createdDate") final String sortField,
                        @Parameter(description = "Sort direction") @RequestParam(value = "sortDirection", defaultValue = "DESC") final Direction direction,
                        @Parameter(description = "Filters") @RequestParam(value = "filters", required = false) final List<String> filters,
                        @Parameter(description = "Global filter") @RequestParam(value = "globalFilter", defaultValue = "") final String globalFilter) {

                QueryParams requestParams = buildQueryParams(pageIndex, pageSize, sortField, direction,
                                filters,
                                globalFilter);

                Page<Espace> espacePage = espaceService.getEntityList(requestParams);

                return buildResponseEntity(
                                espacePage.getContent(), EspaceDto.class,
                                adjustQueryParamsToGetAllRecords(requestParams, espacePage),
                                HttpStatus.OK);
        }

        /**
         * Get espace details by ID (Admin).
         */
        @Operation(summary = "Get " + ENTITY_NAME + " by id (Admin)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ok", content = {
                                        @Content(mediaType = "application/json", schema = @Schema(implementation = EspaceDetailsDto.class)) }),
                        @ApiResponse(responseCode = "404", description = ENTITY_NAME
                                        + " not found", content = @Content(mediaType = "application/json")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "application/json"))
        })
        @GetMapping("/{id}")
        @PreAuthorize("hasAuthority('espace:read')")
        public ResponseEntity<RestResponse<EspaceDetailsDto>> getEspace(@PathVariable("id") final Long id) {
                try {
                        Optional<Espace> espaceOpt = espaceService.findById(id);
                        if (espaceOpt.isEmpty()) {
                                return ResponseEntity.notFound()
                                                .build();
                        }

                        Espace espace = espaceOpt.get();

                        return buildResponseEntity(espace, EspaceDetailsDto.class, HttpStatus.OK);
                } catch (Exception e) {
                        return createNotFoundResponse("Espace with id " + id + " not found");
                }
        }

        // ==================== Abstract Method Implementation ====================

        /**
         * Maps Espace entity to the specified DTO class.
         * Supports EspaceDto and EspaceDetailsDto mappings.
         * 
         * @param entity   The Espace entity to map
         * @param dtoClass The target DTO class
         * @return Mapped DTO instance
         * @throws IllegalArgumentException if unsupported DTO type is provided
         */
        @Override
        protected <DTO> DTO mapToDto(final Espace entity, final Class<DTO> dtoClass) {
                if (dtoClass == EspaceDetailsDto.class) {
                        return dtoClass.cast(espaceDetailMapper.mapToDto(entity, roleAccesService));
                } else if (dtoClass == EspaceDto.class) {
                        return dtoClass.cast(espaceDtoMapper.mapToDto(entity, roleAccesService));
                }

                throw new IllegalArgumentException("Unsupported DTO type: " + dtoClass.getName());
        }
}
