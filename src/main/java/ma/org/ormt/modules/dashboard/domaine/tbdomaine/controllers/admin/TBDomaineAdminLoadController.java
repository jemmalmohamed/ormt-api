package ma.org.ormt.modules.dashboard.domaine.tbdomaine.controllers.admin;

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
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import ma.org.ormt.core.commun.base.controller.BaseController;
import ma.org.ormt.core.commun.rest.queries.QueryParams;
import ma.org.ormt.core.commun.rest.responses.RestResponse;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.dtos.TBDomaineDto;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.dtos.TBDomaineDtoMapper;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.dtos.details.TBDomaineDetailDto;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.dtos.details.TBDomaineDetailDtoMapper;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.models.TBDomaine;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.services.TBDomaineService;
import ma.org.ormt.modules.indicateurs.indicateur.services.indicateur.IndicateurService;

@RestController
@RequestMapping("api/v1/admin/tb-domaines")
@RequiredArgsConstructor
public class TBDomaineAdminLoadController extends BaseController<TBDomaine> {

        private static final String ENTITY_NAME = "TBdomaine";

        private final TBDomaineService domaineService;
        private final TBDomaineDtoMapper tbDomaineDtoMapper;
        private final TBDomaineDetailDtoMapper tbDomaineDetailMapper;
        private final IndicateurService indicateurService;

        @Operation(summary = "Get all " + ENTITY_NAME + "s")
        @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Ok", content = {
                        @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = TBDomaineDto.class))) }),
                        @ApiResponse(responseCode = "404", description = ENTITY_NAME
                                        + " not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @GetMapping("")
        @PreAuthorize("hasAuthority('dashboard:list')")
        public ResponseEntity<RestResponse<List<TBDomaineDto>>> getDomaines(
                        @RequestParam(value = "pageIndex", defaultValue = "0") int pageIndex,
                        @RequestParam(value = "pageSize", defaultValue = "-1") int pageSize,
                        @RequestParam(value = "sortField", defaultValue = "createdDate") String sortField,
                        @RequestParam(value = "sortDirection", defaultValue = "DESC") Direction direction,
                        @RequestParam(value = "filters", defaultValue = "") List<String> filters,
                        @RequestParam(value = "globalFilter", defaultValue = "") String globalFilter) {

                QueryParams requestParams = buildQueryParams(pageIndex, pageSize, sortField, direction,
                                filters,
                                globalFilter);

                Page<TBDomaine> domainePage = domaineService.getEntityList(requestParams);
                return buildResponseEntity(
                                domainePage.getContent(), TBDomaineDto.class,
                                adjustQueryParamsToGetAllRecords(requestParams, domainePage),
                                HttpStatus.OK, true);
        }

        // @Operation(summary = "Get " + ENTITY_NAME + " by id")
        // @ApiResponses(value = {
        // @ApiResponse(responseCode = "200", description = "Ok", content = {
        // @Content(mediaType = "application/json", schema = @Schema(implementation =
        // TBDomaineDto.class)) }),
        // @ApiResponse(responseCode = "404", description = ENTITY_NAME
        // + " not found", content = @Content(mediaType = "ErrorResponse")),
        // @ApiResponse(responseCode = "403", description = "Permission denied", content
        // = @Content(mediaType = "ErrorResponse"))
        // })
        // @GetMapping("/{id}")
        // @PreAuthorize("hasAuthority('dashboard:read')")
        // public ResponseEntity<RestResponse<TBDomaineDetailDto>>
        // getDomaine(@PathVariable("id") Long id) {
        // TBDomaine tbDomaine =
        // domaineService.findById(id).orElseThrow(EntityNotFoundException::new);
        // return buildResponseEntity(tbDomaine, TBDomaineDetailDto.class,
        // HttpStatus.OK);

        // }

        @Operation(summary = "Get " + ENTITY_NAME
                        + " with pivot table data", description = "Get TB domaine details with table data for indicateurs. "
                                        +
                                        "Use tableFormat parameter: 'pivot' for pivot table, 'flat' for flat table, 'crud' for CRUD operations, "
                                        +
                                        "'create' for create template, 'both' for pivot+flat, 'all' for all formats")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ok", content = {
                                        @Content(mediaType = "application/json", schema = @Schema(implementation = TBDomaineDetailDto.class)) }),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @GetMapping("/{id}")
        @PreAuthorize("hasAuthority('dashboard:read')")
        public ResponseEntity<RestResponse<TBDomaineDetailDto>> getTBDomaineWithPivotTable(
                        @PathVariable("id") Long id,
                        @Parameter(description = "Table format: 'pivot', 'flat', 'crud', 'create', 'both', or 'all'", example = "pivot") @RequestParam(value = "tableFormat", defaultValue = "pivot") String tableFormat) {
                TBDomaineDetailDto dto = domaineService.getTBDomaineWithPivotTable(id, tableFormat);
                return ResponseEntity.ok(RestResponse.<TBDomaineDetailDto>builder().data(dto).build());
        }

        @Operation(summary = "Get all " + ENTITY_NAME
                        + "s with pivot table data", description = "List TB domaines with table data for indicateurs. "
                                        +
                                        "Use tableFormat parameter: 'pivot' for pivot table, 'flat' for flat table, 'crud' for CRUD operations, "
                                        +
                                        "'create' for create template, 'both' for pivot+flat, 'all' for all formats")
        @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Ok", content = {
                        @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = TBDomaineDetailDto.class))) }),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @GetMapping("/pivot-table")
        @PreAuthorize("hasAuthority('dashboard:list')")
        public ResponseEntity<RestResponse<List<TBDomaineDetailDto>>> getTBDomainesWithPivotTable(
                        @RequestParam(value = "pageIndex", defaultValue = "0") int pageIndex,
                        @RequestParam(value = "pageSize", defaultValue = "-1") int pageSize,
                        @RequestParam(value = "sortField", defaultValue = "createdDate") String sortField,
                        @RequestParam(value = "sortDirection", defaultValue = "DESC") Direction direction,
                        @RequestParam(value = "filters", defaultValue = "") List<String> filters,
                        @RequestParam(value = "globalFilter", defaultValue = "") String globalFilter,
                        @Parameter(description = "Table format: 'pivot', 'flat', 'crud', 'create', 'both', or 'all'", example = "pivot") @RequestParam(value = "tableFormat", defaultValue = "pivot") String tableFormat) {

                QueryParams requestParams = buildQueryParams(pageIndex, pageSize, sortField, direction, filters,
                                globalFilter);

                List<TBDomaineDetailDto> data = domaineService.getTBDomainesWithPivotTable(requestParams,
                                tableFormat);
                return ResponseEntity.ok(RestResponse.<List<TBDomaineDetailDto>>builder().data(data).build());
        }

        @Override
        protected <DTO> DTO mapToDto(TBDomaine entity, Class<DTO> dtoClass) {
                if (dtoClass == TBDomaineDetailDto.class) {
                        return dtoClass.cast(tbDomaineDetailMapper.mapToDto(entity, indicateurService));
                } else if (dtoClass == TBDomaineDto.class) {
                        return dtoClass.cast(tbDomaineDtoMapper.mapToDto(entity, indicateurService));
                }
                throw new IllegalArgumentException("Unsupported DTO type: " + dtoClass.getName());
        }

}
