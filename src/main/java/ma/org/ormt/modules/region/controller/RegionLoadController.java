package ma.org.ormt.modules.region.controller;

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
import ma.org.ormt.modules.region.Region;
import ma.org.ormt.modules.region.dto.RegionDto;
import ma.org.ormt.modules.region.dto.RegionDtoMapper;
import ma.org.ormt.modules.region.dto.detail.RegionDetailDto;
import ma.org.ormt.modules.region.dto.detail.RegionDetailMapper;
import ma.org.ormt.modules.region.service.RegionService;

@RestController
@RequestMapping("api/v1/regions")
@RequiredArgsConstructor
public class RegionLoadController extends BaseController<Region> {

        private static final String ENTITY_NAME = "region";

        private final RegionService regionService;
        private final RegionDtoMapper regionDtoMapper;
        private final RegionDetailMapper regionDetailMapper;

        @Operation(summary = "Get all " + ENTITY_NAME + "s")
        @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Ok", content = {
                        @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = RegionDto.class))) }),
                        @ApiResponse(responseCode = "404", description = ENTITY_NAME
                                        + " not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @GetMapping("")
        @PreAuthorize("hasAuthority('region:list')")
        public ResponseEntity<RestResponse<List<RegionDto>>> getRegions(
                        @RequestParam(value = "pageIndex", defaultValue = "0") int pageIndex,
                        @RequestParam(value = "pageSize", defaultValue = "-1") int pageSize,
                        @RequestParam(value = "sortField", defaultValue = "createdDate") String sortField,
                        @RequestParam(value = "sortDirection", defaultValue = "DESC") Direction direction,
                        @RequestParam(value = "filters", defaultValue = "") List<String> filters,
                        @RequestParam(value = "globalFilter", defaultValue = "") String globalFilter) {

                QueryParams requestParams = createQueryParams(pageIndex, pageSize, sortField, direction, filters,
                                globalFilter);

                Page<Region> regionPage = regionService.getEntityList(requestParams);

                List<RegionDto> dtos = regionDtoMapper.mapToDto(regionPage.getContent());

                QueryParams queryParams = adjustQueryParamsForAllRecords(requestParams, regionPage);

                return buildResponseEntity(dtos, queryParams, HttpStatus.OK);

        }

        @Operation(summary = "Get " + ENTITY_NAME + " by id")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ok", content = {
                                        @Content(mediaType = "application/json", schema = @Schema(implementation = RegionDto.class)) }),
                        @ApiResponse(responseCode = "404", description = ENTITY_NAME
                                        + " not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @GetMapping("/{id}")
        @PreAuthorize("hasAuthority('region:read')")
        public ResponseEntity<RestResponse<RegionDetailDto>> getRegion(@PathVariable("id") Long id) {
                Region region = regionService.findById(id).orElseThrow(EntityNotFoundException::new);
                return buildResponseEntity(region, RegionDetailDto.class, HttpStatus.OK);

        }

        @Override
        protected <DTO> DTO mapToDto(Region entity, Class<DTO> dtoClass) {
                if (dtoClass == RegionDetailDto.class) {
                        return dtoClass.cast(regionDetailMapper.mapToDto(entity));
                } else if (dtoClass == RegionDto.class) {
                        return dtoClass.cast(regionDtoMapper.mapToDto(entity));
                }
                throw new IllegalArgumentException("Unsupported DTO type: " + dtoClass.getName());
        }

}
