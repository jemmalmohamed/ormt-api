package ma.org.ormt.modules.dashboard.tableaubord.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import ma.org.ormt.modules.dashboard.tableaubord.dtos.TableauBordDto;
import ma.org.ormt.modules.dashboard.tableaubord.dtos.TableauBordDtoMapper;
import ma.org.ormt.modules.dashboard.tableaubord.dtos.details.TableauBordDetailsDto;
import ma.org.ormt.modules.dashboard.tableaubord.dtos.details.TableauBordDetailsDtoMapper;
import ma.org.ormt.modules.dashboard.tableaubord.models.TableauBord;
import ma.org.ormt.modules.dashboard.tableaubord.services.TableauBordService;

@Slf4j
@Validated
@RestController
@RequestMapping("api/v1/public/dashboards")
@RequiredArgsConstructor
public class TableauBordPublicLoadController extends BaseController<TableauBord> {

        private static final String ENTITY_NAME = "tableauBord";
        private static final String RESOURCE_TYPE = "tableauBord";

        private final TableauBordService tableauBordService;
        private final TableauBordDtoMapper tableauBordDtoMapper;
        private final TableauBordDetailsDtoMapper tableauBordDetailMapper;

        /**
         * Get all tableauxBords with optional pagination, sorting, and filtering.
         */
        @Operation(summary = "Get all " + ENTITY_NAME + "s")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ok", content = {
                                        @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = TableauBordDto.class))) }),
                        @ApiResponse(responseCode = "404", description = ENTITY_NAME
                                        + " not found", content = @Content(mediaType = "application/json")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "application/json"))
        })
        @GetMapping
        public ResponseEntity<RestResponse<List<TableauBordDto>>> getTableauxBord(
                        @Parameter(description = "Page index (0-based)") @RequestParam(value = "pageIndex", defaultValue = "0") final int pageIndex,
                        @Parameter(description = "Page size (-1 for all)") @RequestParam(value = "pageSize", defaultValue = "-1") final int pageSize,
                        @Parameter(description = "Sort field") @RequestParam(value = "sortField", defaultValue = "createdDate") final String sortField,
                        @Parameter(description = "Sort direction") @RequestParam(value = "sortDirection", defaultValue = "DESC") final Direction direction,
                        @Parameter(description = "Filters") @RequestParam(value = "filters", required = false) final List<String> filters,
                        @Parameter(description = "Global filter") @RequestParam(value = "globalFilter", defaultValue = "") final String globalFilter) {

                // Ensure filters is not null and add 'actif:true'
                List<String> effectiveFilters = (filters == null) ? new java.util.ArrayList<>()
                                : new java.util.ArrayList<>(filters);

                effectiveFilters.add("actif:like:true");

                QueryParams requestParams = buildQueryParams(pageIndex, pageSize, sortField, direction,
                                effectiveFilters,
                                globalFilter);

                // Only dashboards accessible by the current role (exclude role_public)
                Page<TableauBord> tableauBordPage;
                List<Long> accessibleIds = roleAccesService
                                .getAccessibleResourceIdsForCurrentUser(RESOURCE_TYPE, "lecture");
                if (accessibleIds == null) {
                        // Admin/Master - access to all
                        tableauBordPage = tableauBordService.getEntityList(requestParams);
                } else if (accessibleIds.isEmpty()) {
                        tableauBordPage = Page.empty();
                } else {
                        tableauBordPage = tableauBordService.getEntitiesByIds(accessibleIds, requestParams);
                }

                return buildResponseEntity(
                                tableauBordPage.getContent(), TableauBordDto.class,
                                adjustQueryParamsToGetAllRecords(requestParams, tableauBordPage),
                                HttpStatus.OK, true);
        }

        /**
         * Get tableaTableauBord details by ID.
         */
        @Operation(summary = "Get " + ENTITY_NAME + " by id")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ok", content = {
                                        @Content(mediaType = "application/json", schema = @Schema(implementation = TableauBordDetailsDto.class)) }),
                        @ApiResponse(responseCode = "404", description = ENTITY_NAME
                                        + " not found", content = @Content(mediaType = "application/json")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "application/json"))
        })
        @GetMapping("/{id}")
        public ResponseEntity<RestResponse<TableauBordDetailsDto>> getTableauBord(@PathVariable("id") final Long id) {
                // Require direct role access; no public fallback
                boolean hasResourceAccess = roleAccesService.hasAccessToResource(id, RESOURCE_TYPE, "lecture");
                if (!hasResourceAccess) {
                        return createForbiddenResponse();
                }

                try {
                        Optional<TableauBord> tableaTableauBordOpt = tableauBordService.findById(id);
                        if (tableaTableauBordOpt.isEmpty()) {
                                return ResponseEntity.notFound()
                                                .build();
                        }

                        TableauBord tableaTableauBord = tableaTableauBordOpt.get();
                        if (!tableaTableauBord.isActif()) {
                                return ResponseEntity.notFound()
                                                .build();
                        }

                        return buildResponseEntity(tableaTableauBord, TableauBordDetailsDto.class, HttpStatus.OK);
                } catch (Exception e) {
                        return createNotFoundResponse("TableauBord with id " + id + " not found");
                }
        }

        // ==================== Abstract Method Implementation ====================

        /**
         * Maps TableauBord entity to the specified DTO class.
         * Supports TableauBordDto and TableauBordDetailsDto mappings.
         * 
         * @param entity   The TableauBord entity to map
         * @param dtoClass The target DTO class
         * @return Mapped DTO instance
         * @throws IllegalArgumentException if unsupported DTO type is provided
         */
        @Override
        protected <DTO> DTO mapToDto(final TableauBord entity, final Class<DTO> dtoClass) {
                if (dtoClass == TableauBordDetailsDto.class) {
                        return dtoClass.cast(tableauBordDetailMapper.mapToDto(entity, roleAccesService));
                } else if (dtoClass == TableauBordDto.class) {
                        return dtoClass.cast(tableauBordDtoMapper.mapToDto(entity, roleAccesService));
                }

                throw new IllegalArgumentException("Unsupported DTO type: " + dtoClass.getName());
        }
}