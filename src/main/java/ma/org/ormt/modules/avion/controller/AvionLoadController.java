package ma.org.ormt.modules.avion.controller;

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
import ma.org.ormt.modules.avion.Avion;
import ma.org.ormt.modules.avion.dto.AvionDto;
import ma.org.ormt.modules.avion.dto.AvionDtoMapper;
import ma.org.ormt.modules.avion.dto.detail.AvionDetailDto;
import ma.org.ormt.modules.avion.dto.detail.AvionDetailMapper;
import ma.org.ormt.modules.avion.service.AvionService;

@RestController
@RequestMapping("api/v1/avions")
@RequiredArgsConstructor
public class AvionLoadController extends BaseController<Avion> {

        private static final String ENTITY_NAME = "avion";

        private final AvionService avionService;
        private final AvionDtoMapper avionDtoMapper;
        private final AvionDetailMapper avionDetailMapper;

        @Operation(summary = "Get all " + ENTITY_NAME + "s")
        @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Ok", content = {
                        @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = AvionDto.class))) }),
                        @ApiResponse(responseCode = "404", description = ENTITY_NAME
                                        + " not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @GetMapping("")
        @PreAuthorize("hasAuthority('avion:list')")
        public ResponseEntity<RestResponse<List<AvionDto>>> getAvions(
                        @RequestParam(value = "pageIndex", defaultValue = "0") int pageIndex,
                        @RequestParam(value = "pageSize", defaultValue = "-1") int pageSize,
                        @RequestParam(value = "sortField", defaultValue = "createdDate") String sortField,
                        @RequestParam(value = "sortDirection", defaultValue = "DESC") Direction direction,
                        @RequestParam(value = "filters", defaultValue = "") List<String> filters,
                        @RequestParam(value = "globalFilter", defaultValue = "") String globalFilter) {

                QueryParams requestParams = createQueryParams(pageIndex, pageSize, sortField, direction, filters,
                                globalFilter);

                Page<Avion> avionPage = avionService.getEntityList(requestParams);

                List<AvionDto> dtos = avionDtoMapper.mapToDto(avionPage.getContent());

                QueryParams queryParams = adjustQueryParamsForAllRecords(requestParams, avionPage);

                return buildResponseEntity(dtos, queryParams, HttpStatus.OK);

        }

        @Operation(summary = "Get " + ENTITY_NAME + " by id")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ok", content = {
                                        @Content(mediaType = "application/json", schema = @Schema(implementation = AvionDto.class)) }),
                        @ApiResponse(responseCode = "404", description = ENTITY_NAME
                                        + " not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @GetMapping("/{id}")
        @PreAuthorize("hasAuthority('avion:read')")
        public ResponseEntity<RestResponse<AvionDetailDto>> getAvion(@PathVariable("id") Long id) {
                Avion avion = avionService.findById(id).orElseThrow(EntityNotFoundException::new);
                return buildResponseEntity(avion, AvionDetailDto.class, HttpStatus.OK);

        }

        @Override
        protected <DTO> DTO mapToDto(Avion entity, Class<DTO> dtoClass) {
                if (dtoClass == AvionDetailDto.class) {
                        return dtoClass.cast(avionDetailMapper.mapToDto(entity));
                } else if (dtoClass == AvionDto.class) {
                        return dtoClass.cast(avionDtoMapper.mapToDto(entity));
                }
                throw new IllegalArgumentException("Unsupported DTO type: " + dtoClass.getName());
        }

}
