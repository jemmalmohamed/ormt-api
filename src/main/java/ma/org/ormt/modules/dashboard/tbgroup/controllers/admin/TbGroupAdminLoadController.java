package ma.org.ormt.modules.dashboard.tbgroup.controllers.admin;

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
import ma.org.ormt.modules.dashboard.tbgroup.dtos.TbGroupDto;
import ma.org.ormt.modules.dashboard.tbgroup.dtos.TbGroupDtoMapper;
import ma.org.ormt.modules.dashboard.tbgroup.dtos.details.TbGroupDetailsDto;
import ma.org.ormt.modules.dashboard.tbgroup.dtos.details.TbGroupDetailsDtoMapper;
import ma.org.ormt.modules.dashboard.tbgroup.models.TbGroup;
import ma.org.ormt.modules.dashboard.tbgroup.services.TbGroupService;

@Slf4j
@Validated
@RestController
@RequestMapping("api/v1/admin/tb-groups")
@RequiredArgsConstructor
public class TbGroupAdminLoadController extends BaseController<TbGroup> {

        private static final String ENTITY_NAME = "tb_group";

        private final TbGroupService tbGroupService;
        private final TbGroupDtoMapper tbGroupDtoMapper;
        private final TbGroupDetailsDtoMapper tbGroupDetailMapper;

        /**
         * Get all tb_groups with optional pagination, sorting, and filtering
         * (Admin).
         */
        @Operation(summary = "Get all " + ENTITY_NAME + "s (Admin)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ok", content = {
                                        @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = TbGroupDto.class))) }),
                        @ApiResponse(responseCode = "404", description = ENTITY_NAME
                                        + " not found", content = @Content(mediaType = "application/json")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "application/json"))
        })
        @GetMapping
        @PreAuthorize("hasAuthority('dashboard:list')")
        public ResponseEntity<RestResponse<List<TbGroupDto>>> getTableauxBord(
                        @Parameter(description = "Page index (0-based)") @RequestParam(value = "pageIndex", defaultValue = "0") final int pageIndex,
                        @Parameter(description = "Page size (-1 for all)") @RequestParam(value = "pageSize", defaultValue = "-1") final int pageSize,
                        @Parameter(description = "Sort field") @RequestParam(value = "sortField", defaultValue = "createdDate") final String sortField,
                        @Parameter(description = "Sort direction") @RequestParam(value = "sortDirection", defaultValue = "DESC") final Direction direction,
                        @Parameter(description = "Filters") @RequestParam(value = "filters", required = false) final List<String> filters,
                        @Parameter(description = "Global filter") @RequestParam(value = "globalFilter", defaultValue = "") final String globalFilter) {

                QueryParams requestParams = buildQueryParams(pageIndex, pageSize, sortField, direction,
                                filters,
                                globalFilter);

                Page<TbGroup> tbGroupPage = tbGroupService.getEntityList(requestParams);

                return buildResponseEntity(
                                tbGroupPage.getContent(), TbGroupDto.class,
                                adjustQueryParamsToGetAllRecords(requestParams, tbGroupPage),
                                HttpStatus.OK, true);
        }

        /**
         * Get tb_group details by ID (Admin).
         */
        @Operation(summary = "Get " + ENTITY_NAME + " by id (Admin)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ok", content = {
                                        @Content(mediaType = "application/json", schema = @Schema(implementation = TbGroupDetailsDto.class)) }),
                        @ApiResponse(responseCode = "404", description = ENTITY_NAME
                                        + " not found", content = @Content(mediaType = "application/json")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "application/json"))
        })
        @GetMapping("/{id}")
        @PreAuthorize("hasAuthority('dashboard:read')")
        public ResponseEntity<RestResponse<TbGroupDetailsDto>> getTbGroup(@PathVariable("id") final Long id) {
                try {
                        Optional<TbGroup> tbGroupOpt = tbGroupService.findById(id);
                        if (tbGroupOpt.isEmpty()) {
                                return ResponseEntity.notFound()
                                                .build();
                        }

                        TbGroup tbGroup = tbGroupOpt.get();

                        return buildResponseEntity(tbGroup, TbGroupDetailsDto.class, HttpStatus.OK);
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
