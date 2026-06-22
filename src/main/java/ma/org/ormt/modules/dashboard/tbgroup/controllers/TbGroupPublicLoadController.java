package ma.org.ormt.modules.dashboard.tbgroup.controllers;

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
import ma.org.ormt.modules.dashboard.tbgroup.dtos.TbGroupDto;
import ma.org.ormt.modules.dashboard.tbgroup.dtos.TbGroupDtoMapper;
import ma.org.ormt.modules.dashboard.tbgroup.dtos.details.TbGroupDetailsDto;
import ma.org.ormt.modules.dashboard.tbgroup.dtos.details.TbGroupDetailsDtoMapper;
import ma.org.ormt.modules.dashboard.tbgroup.models.TbGroup;
import ma.org.ormt.modules.dashboard.tbgroup.services.TbGroupService;

@Slf4j
@Validated
@RestController
@RequestMapping("api/v1/public/tb-groups")
@RequiredArgsConstructor
public class TbGroupPublicLoadController extends BaseController<TbGroup> {

        private static final String ENTITY_NAME = "tbGroup";
        private static final String RESOURCE_TYPE = "tbGroup";

        private final TbGroupService tbGroupService;
        private final TbGroupDtoMapper tbGroupDtoMapper;
        private final TbGroupDetailsDtoMapper tbGroupDetailMapper;

        /**
         * Get all tb_groups with optional pagination, sorting, and filtering.
         */
        @Operation(summary = "Get all " + ENTITY_NAME + "s")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ok", content = {
                                        @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = TbGroupDto.class))) }),
                        @ApiResponse(responseCode = "404", description = ENTITY_NAME
                                        + " not found", content = @Content(mediaType = "application/json")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "application/json"))
        })
        @GetMapping
        public ResponseEntity<RestResponse<List<TbGroupDto>>> getTableauxBord(
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
                Page<TbGroup> tbGroupPage;
                List<Long> accessibleIds = roleAccesService
                                .getAccessibleResourceIdsForCurrentUser(RESOURCE_TYPE, "lecture");
                if (accessibleIds == null) {
                        // Admin/Master - access to all
                        tbGroupPage = tbGroupService.getEntityList(requestParams);
                } else if (accessibleIds.isEmpty()) {
                        tbGroupPage = Page.empty();
                } else {
                        tbGroupPage = tbGroupService.getEntitiesByIds(accessibleIds, requestParams);
                }

                return buildResponseEntity(
                                tbGroupPage.getContent(), TbGroupDto.class,
                                adjustQueryParamsToGetAllRecords(requestParams, tbGroupPage),
                                HttpStatus.OK, true);
        }

        /**
         * Get tb_group details by ID.
         */
        @Operation(summary = "Get " + ENTITY_NAME + " by id")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ok", content = {
                                        @Content(mediaType = "application/json", schema = @Schema(implementation = TbGroupDetailsDto.class)) }),
                        @ApiResponse(responseCode = "404", description = ENTITY_NAME
                                        + " not found", content = @Content(mediaType = "application/json")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "application/json"))
        })
        @GetMapping("/{id}")
        public ResponseEntity<RestResponse<TbGroupDetailsDto>> getTbGroup(@PathVariable("id") final Long id) {
                // Require direct role access; no public fallback
                boolean hasResourceAccess = roleAccesService.hasAccessToResource(id, RESOURCE_TYPE, "lecture");
                if (!hasResourceAccess) {
                        return createForbiddenResponse();
                }

                try {
                        Optional<TbGroup> tableaTbGroupOpt = tbGroupService.findById(id);
                        if (tableaTbGroupOpt.isEmpty()) {
                                return ResponseEntity.notFound()
                                                .build();
                        }

                        TbGroup tableaTbGroup = tableaTbGroupOpt.get();
                        if (!tableaTbGroup.isActif()) {
                                return ResponseEntity.notFound()
                                                .build();
                        }

                        return buildResponseEntity(tableaTbGroup, TbGroupDetailsDto.class, HttpStatus.OK);
                } catch (Exception e) {
                        return createNotFoundResponse("TB group with id " + id + " not found");
                }
        }

        // ==================== Abstract Method Implementation ====================

        /**
         * Maps TbGroup entity to the specified DTO class.
         * Supports TbGroupDto and TbGroupDetailsDto mappings.
         * 
         * @param entity   The TbGroup entity to map
         * @param dtoClass The target DTO class
         * @return Mapped DTO instance
         * @throws IllegalArgumentException if unsupported DTO type is provided
         */
        @Override
        protected <DTO> DTO mapToDto(final TbGroup entity, final Class<DTO> dtoClass) {
                if (dtoClass == TbGroupDetailsDto.class) {
                        return dtoClass.cast(tbGroupDetailMapper.mapToDto(entity, roleAccesService));
                } else if (dtoClass == TbGroupDto.class) {
                        return dtoClass.cast(tbGroupDtoMapper.mapToDto(entity, roleAccesService));
                }

                throw new IllegalArgumentException("Unsupported DTO type: " + dtoClass.getName());
        }
}
