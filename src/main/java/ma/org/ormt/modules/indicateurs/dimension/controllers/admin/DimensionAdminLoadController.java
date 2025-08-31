package ma.org.ormt.modules.indicateurs.dimension.controllers.admin;

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
import ma.org.ormt.modules.indicateurs.dimension.dtos.DimensionDto;
import ma.org.ormt.modules.indicateurs.dimension.dtos.DimensionDtoMapper;
import ma.org.ormt.modules.indicateurs.dimension.dtos.details.DimensionDetailsDto;
import ma.org.ormt.modules.indicateurs.dimension.dtos.details.DimensionDetailsDtoMapper;
import ma.org.ormt.modules.indicateurs.dimension.models.Dimension;
import ma.org.ormt.modules.indicateurs.dimension.services.DimensionService;

@RestController
@RequestMapping("api/v1/admin/dimensions")
@RequiredArgsConstructor
public class DimensionAdminLoadController extends BaseController<Dimension> {

        private static final String ENTITY_NAME = "dimension";

        private final DimensionService dimensionService;
        private final DimensionDtoMapper dimensionDtoMapper;
        private final DimensionDetailsDtoMapper dimensionDetailMapper;

        @Operation(summary = "Get all " + ENTITY_NAME + "s")
        @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Ok", content = {
                        @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = DimensionDto.class))) }),
                        @ApiResponse(responseCode = "404", description = ENTITY_NAME
                                        + " not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @GetMapping("")
        @PreAuthorize("hasAuthority('dimension:list')")
        public ResponseEntity<RestResponse<List<DimensionDto>>> getDimensions(
                        @RequestParam(value = "pageIndex", defaultValue = "0") int pageIndex,
                        @RequestParam(value = "pageSize", defaultValue = "-1") int pageSize,
                        @RequestParam(value = "sortField", defaultValue = "createdDate") String sortField,
                        @RequestParam(value = "sortDirection", defaultValue = "DESC") Direction direction,
                        @RequestParam(value = "filters", defaultValue = "") List<String> filters,
                        @RequestParam(value = "globalFilter", defaultValue = "") String globalFilter) {

                QueryParams requestParams = buildQueryParams(pageIndex, pageSize, sortField, direction, filters,
                                globalFilter);

                Page<Dimension> dimensionPage = dimensionService.getEntityList(requestParams);

                QueryParams queryParams = adjustQueryParamsToGetAllRecords(requestParams, dimensionPage);

                return buildResponseEntity(dimensionPage.getContent(), DimensionDto.class, queryParams, HttpStatus.OK,
                                true);
        }

        @Operation(summary = "Get " + ENTITY_NAME + " by id")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ok", content = {
                                        @Content(mediaType = "application/json", schema = @Schema(implementation = DimensionDto.class)) }),
                        @ApiResponse(responseCode = "404", description = ENTITY_NAME
                                        + " not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @GetMapping("/{id}")
        @PreAuthorize("hasAuthority('dimension:read')")
        public ResponseEntity<RestResponse<DimensionDetailsDto>> getDimension(@PathVariable("id") Long id) {
                Dimension dimension = dimensionService.findById(id).orElseThrow(EntityNotFoundException::new);
                return buildResponseEntity(dimension, DimensionDetailsDto.class, HttpStatus.OK);
        }

        @Override
        protected <DTO> DTO mapToDto(Dimension entity, Class<DTO> dtoClass) {
                if (dtoClass == DimensionDetailsDto.class) {
                        return dtoClass.cast(dimensionDetailMapper.mapToDto(entity));
                } else if (dtoClass == DimensionDto.class) {
                        return dtoClass.cast(dimensionDtoMapper.mapToDto(entity));
                }
                throw new IllegalArgumentException("Unsupported DTO type: " + dtoClass.getName());
        }
}