package ma.org.ormt.modules.chiffres.controllers;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
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
import ma.org.ormt.modules.chiffres.dtos.ChiffreCleDto;
import ma.org.ormt.modules.chiffres.dtos.ChiffreCleDtoMapper;
import ma.org.ormt.modules.chiffres.dtos.details.ChiffreCleDetailsDto;
import ma.org.ormt.modules.chiffres.dtos.details.ChiffreCleDetailsDtoMapper;
import ma.org.ormt.modules.chiffres.models.ChiffreCle;
import ma.org.ormt.modules.chiffres.services.ChiffreCleService;

@Slf4j
@Validated
@RestController
@RequestMapping("api/v1/public/chiffrecles")
@RequiredArgsConstructor
public class ChiffreCleLoadPublicController extends BaseController<ChiffreCle> {

        private static final String ENTITY_NAME = "chiffreCle";
        private static final String RESOURCE_TYPE = "chiffreCle";

        private final ChiffreCleService chiffreCleService;
        private final ChiffreCleDtoMapper chiffreCleDtoMapper;
        private final ChiffreCleDetailsDtoMapper chiffreCleDetailsMapper;

        /**
         * Get all chiffresCles with optional pagination, sorting, and filtering.
         */
        @Operation(summary = "Get all " + ENTITY_NAME + "s")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ok", content = {
                                        @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ChiffreCleDto.class))) }),
                        @ApiResponse(responseCode = "404", description = ENTITY_NAME
                                        + " not found", content = @Content(mediaType = "application/json")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "application/json"))
        })
        @GetMapping
        public ResponseEntity<RestResponse<List<ChiffreCleDto>>> getChiffreCles(
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

                Page<ChiffreCle> chiffreClePage = getEntitiesWithAccessControl(
                                RESOURCE_TYPE,
                                "lecture",
                                requestParams,
                                chiffreCleService::getEntityList, // Function<QueryParams, Page<T>>
                                chiffreCleService::getEntitiesByIds // BiFunction<List<Long>, QueryParams, Page<T>>
                );

                return buildResponseEntity(
                                chiffreClePage.getContent(), ChiffreCleDto.class,
                                adjustQueryParamsToGetAllRecords(requestParams, chiffreClePage),
                                HttpStatus.OK, true);
        }

        // /**
        // * Get chiffreCle details by ID.
        // */
        // @Operation(summary = "Get " + ENTITY_NAME + " by id")
        // @ApiResponses(value = {
        // @ApiResponse(responseCode = "200", description = "Ok", content = {
        // @Content(mediaType = "application/json", schema = @Schema(implementation =
        // ChiffreCleDetailsDto.class)) }),
        // @ApiResponse(responseCode = "404", description = ENTITY_NAME
        // + " not found", content = @Content(mediaType = "application/json")),
        // @ApiResponse(responseCode = "403", description = "Permission denied", content
        // = @Content(mediaType = "application/json"))
        // })
        // @GetMapping("/{id}")
        // @PreAuthorize("hasAuthority('chiffrecle:read')")
        // public ResponseEntity<RestResponse<ChiffreCleDetailsDto>>
        // getChiffreCle(@PathVariable("id") final Long id) {
        // boolean hasResourceAccess = hasResourceAccess(id, RESOURCE_TYPE, "lecture");
        // if (!hasResourceAccess) {
        // return createForbiddenResponse();
        // }

        // try {
        // Optional<ChiffreCle> chiffreCleOpt = chiffreCleService.findById(id);
        // if (chiffreCleOpt.isEmpty()) {
        // return ResponseEntity.notFound()
        // .build();
        // }

        // ChiffreCle chiffreCle = chiffreCleOpt.get();
        // if (!chiffreCle.isActif()) {
        // return ResponseEntity.notFound()
        // .build();
        // }

        // return buildResponseEntity(chiffreCle, ChiffreCleDetailsDto.class,
        // HttpStatus.OK);
        // } catch (Exception e) {
        // return createNotFoundResponse("ChiffreCle with id " + id + " not found");
        // }
        // }

        // ==================== Abstract Method Implementation ====================

        /**
         * Maps chiffreCle entity to the specified DTO class.
         * Supports ChiffreCleDto and ChiffreCleDetailsDto mappings.
         * 
         * @param entity   The ChiffreCle entity to map
         * @param dtoClass The target DTO class
         * @return Mapped DTO instance
         * @throws IllegalArgumentException if unsupported DTO type is provided
         */
        @Override
        protected <DTO> DTO mapToDto(final ChiffreCle entity, final Class<DTO> dtoClass) {
                if (dtoClass == ChiffreCleDetailsDto.class) {
                        return dtoClass.cast(chiffreCleDetailsMapper.mapToDto(entity, roleAccesService));
                } else if (dtoClass == ChiffreCleDto.class) {
                        return dtoClass.cast(chiffreCleDtoMapper.mapToDto(entity, roleAccesService));
                }

                throw new IllegalArgumentException("Unsupported DTO type: " + dtoClass.getName());
        }
}