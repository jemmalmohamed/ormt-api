package ma.org.ormt.modules.basemap.controller;

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
import ma.org.ormt.modules.basemap.Basemap;
import ma.org.ormt.modules.basemap.dto.BasemapDto;
import ma.org.ormt.modules.basemap.dto.BasemapDtoMapper;
import ma.org.ormt.modules.basemap.service.BasemapService;

@RestController
@RequestMapping("api/v1/basemaps")
@RequiredArgsConstructor
public class BasemapLoadController extends BaseController<Basemap> {

        private static final String ENTITY_NAME = "basemap";

        private final BasemapService basemapService;
        private final BasemapDtoMapper basemapDtoMapper;

        @Operation(summary = "Get all " + ENTITY_NAME + "s")
        @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Ok", content = {
                        @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BasemapDto.class))) }),
                        @ApiResponse(responseCode = "404", description = ENTITY_NAME
                                        + " not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @GetMapping("")
        // @PreAuthorize("hasAuthority('basemap:list')")
        public ResponseEntity<RestResponse<List<BasemapDto>>> getBasemaps(
                        @RequestParam(value = "pageIndex", defaultValue = "0") int pageIndex,
                        @RequestParam(value = "pageSize", defaultValue = "-1") int pageSize,
                        @RequestParam(value = "sortField", defaultValue = "createdDate") String sortField,
                        @RequestParam(value = "sortDirection", defaultValue = "DESC") Direction direction,
                        @RequestParam(value = "filters", defaultValue = "") List<String> filters,
                        @RequestParam(value = "globalFilter", defaultValue = "") String globalFilter) {

                QueryParams requestParams = createQueryParams(pageIndex, pageSize, sortField, direction, filters,
                                globalFilter);

                Page<Basemap> basemapPage = basemapService.getEntityList(requestParams);

                List<BasemapDto> dtos = basemapDtoMapper.mapToDto(basemapPage.getContent());

                QueryParams queryParams = adjustQueryParamsForAllRecords(requestParams, basemapPage);

                return buildResponseEntity(dtos, queryParams, HttpStatus.OK);

        }

        @Operation(summary = "Get " + ENTITY_NAME + " by id")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ok", content = {
                                        @Content(mediaType = "application/json", schema = @Schema(implementation = BasemapDto.class)) }),
                        @ApiResponse(responseCode = "404", description = ENTITY_NAME
                                        + " not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @GetMapping("/{id}")
        // @PreAuthorize("hasAuthority('basemap:read')")
        public ResponseEntity<RestResponse<BasemapDto>> getBasemap(@PathVariable("id") Long id) {
                Basemap basemap = basemapService.findById(id).orElseThrow(EntityNotFoundException::new);
                return buildResponseEntity(basemap, BasemapDto.class, HttpStatus.OK);

        }

        @Override
        protected <DTO> DTO mapToDto(Basemap entity, Class<DTO> dtoClass) {
                return dtoClass.cast(basemapDtoMapper.mapToDto(entity));
        }

}
