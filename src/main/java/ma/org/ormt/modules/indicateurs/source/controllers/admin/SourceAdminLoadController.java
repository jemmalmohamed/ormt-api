package ma.org.ormt.modules.indicateurs.source.controllers.admin;

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
import ma.org.ormt.modules.indicateurs.source.dtos.SourceDto;
import ma.org.ormt.modules.indicateurs.source.dtos.SourceDtoMapper;
import ma.org.ormt.modules.indicateurs.source.dtos.details.SourceDetailsDto;
import ma.org.ormt.modules.indicateurs.source.dtos.details.SourceDetailsDtoMapper;
import ma.org.ormt.modules.indicateurs.source.models.Source;
import ma.org.ormt.modules.indicateurs.source.services.SourceService;

@RestController
@RequestMapping("api/v1/admin/sources")
@RequiredArgsConstructor
public class SourceAdminLoadController extends BaseController<Source> {

        private static final String ENTITY_NAME = "source";

        private final SourceService sourceService;
        private final SourceDtoMapper sourceDtoMapper;
        private final SourceDetailsDtoMapper sourceDetailMapper;

        @Operation(summary = "Get all " + ENTITY_NAME + "s")
        @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Ok", content = {
                        @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = SourceDto.class))) }),
                        @ApiResponse(responseCode = "404", description = ENTITY_NAME
                                        + " not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @GetMapping("")
        @PreAuthorize("hasAuthority('datasource:list')")
        public ResponseEntity<RestResponse<List<SourceDto>>> getSources(
                        @RequestParam(value = "pageIndex", defaultValue = "0") int pageIndex,
                        @RequestParam(value = "pageSize", defaultValue = "-1") int pageSize,
                        @RequestParam(value = "sortField", defaultValue = "createdDate") String sortField,
                        @RequestParam(value = "sortDirection", defaultValue = "DESC") Direction direction,
                        @RequestParam(value = "filters", defaultValue = "") List<String> filters,
                        @RequestParam(value = "globalFilter", defaultValue = "") String globalFilter) {

                QueryParams requestParams = buildQueryParams(pageIndex, pageSize, sortField, direction, filters,
                                globalFilter);

                Page<Source> sourcePage = sourceService.getEntityList(requestParams);

                QueryParams queryParams = adjustQueryParamsToGetAllRecords(requestParams, sourcePage);

                return buildResponseEntity(sourcePage.getContent(), SourceDto.class, queryParams, HttpStatus.OK,
                                true);
        }

        @Operation(summary = "Get " + ENTITY_NAME + " by id")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ok", content = {
                                        @Content(mediaType = "application/json", schema = @Schema(implementation = SourceDto.class)) }),
                        @ApiResponse(responseCode = "404", description = ENTITY_NAME
                                        + " not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @GetMapping("/{id}")
        @PreAuthorize("hasAuthority('datasource:read')")
        public ResponseEntity<RestResponse<SourceDetailsDto>> getSource(@PathVariable("id") Long id) {
                Source source = sourceService.findById(id).orElseThrow(EntityNotFoundException::new);
                return buildResponseEntity(source, SourceDetailsDto.class, HttpStatus.OK, true);
        }

        @Override
        protected <DTO> DTO mapToDto(Source entity, Class<DTO> dtoClass) {
                if (dtoClass == SourceDetailsDto.class) {
                        return dtoClass.cast(sourceDetailMapper.mapToDto(entity));
                } else if (dtoClass == SourceDto.class) {
                        return dtoClass.cast(sourceDtoMapper.mapToDto(entity));
                }
                throw new IllegalArgumentException("Unsupported DTO type: " + dtoClass.getName());
        }
}