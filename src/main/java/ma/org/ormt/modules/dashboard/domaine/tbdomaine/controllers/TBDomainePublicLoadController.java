package ma.org.ormt.modules.dashboard.domaine.tbdomaine.controllers;

import java.util.List;
import io.swagger.v3.oas.annotations.Parameter;

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
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.dtos.TBDomaineDto;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.dtos.TBDomaineDtoMapper;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.dtos.details.TBDomaineDetailDto;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.dtos.details.TBDomaineDetailDtoMapper;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.models.TBDomaine;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.services.TBDomaineService;

@RestController
@RequestMapping("api/v1/public/dashboards/{tableauBordId}/tb-domaines") // Context-aware public controller
@RequiredArgsConstructor
public class TBDomainePublicLoadController extends BaseController<TBDomaine> {

        private static final String ENTITY_NAME = "tbDomaine";

        private final TBDomaineService tbDomaineService;
        private final TBDomaineDtoMapper tbDomaineDtoMapper;
        private final TBDomaineDetailDtoMapper tbDomaineDetailMapper;

        @Operation(summary = "Get all " + ENTITY_NAME + "s in tableau de bord (context-aware)")
        @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Ok", content = {
                        @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = TBDomaineDto.class))) }),
                        @ApiResponse(responseCode = "404", description = ENTITY_NAME
                                        + " not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @GetMapping("")
        @PreAuthorize("hasAuthority('tableaubord:list')")
        public ResponseEntity<RestResponse<List<TBDomaineDto>>> getDomaines(
                        @PathVariable("tableauBordId") Long tableauBordId,
                        @RequestParam(value = "pageIndex", defaultValue = "0") int pageIndex,
                        @RequestParam(value = "pageSize", defaultValue = "-1") int pageSize,
                        @RequestParam(value = "sortField", defaultValue = "createdDate") String sortField,
                        @RequestParam(value = "sortDirection", defaultValue = "DESC") Direction direction,
                        @RequestParam(value = "filters", defaultValue = "") List<String> filters,
                        @RequestParam(value = "globalFilter", defaultValue = "") String globalFilter) {

                if (!hasResourceAccess(tableauBordId, "tableauBord", "lecture")) {
                        return createForbiddenResponse();
                }

                List<String> effectiveFilters = (filters == null) ? new java.util.ArrayList<>()
                                : new java.util.ArrayList<>(filters);
                effectiveFilters.add("actif:like:true");
                effectiveFilters.add("tableauBordDomaines.tableauBord.id:=:" + tableauBordId);

                QueryParams requestParams = buildQueryParams(pageIndex, pageSize, sortField, direction,
                                effectiveFilters,
                                globalFilter);

                Page<TBDomaine> domainePage = tbDomaineService.getEntityList(requestParams);

                return buildResponseEntity(
                                domainePage.getContent(), TBDomaineDto.class,
                                adjustQueryParamsToGetAllRecords(requestParams, domainePage),
                                HttpStatus.OK, true);
        }

        @Operation(summary = "Get " + ENTITY_NAME + " by id in TB (context-aware)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ok", content = {
                                        @Content(mediaType = "application/json", schema = @Schema(implementation = TBDomaineDetailDto.class)) }),
                        @ApiResponse(responseCode = "404", description = ENTITY_NAME
                                        + " not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @GetMapping("/{id}")
        @PreAuthorize("hasAuthority('tableaubord:read')")
        public ResponseEntity<RestResponse<TBDomaineDetailDto>> getDomaine(
                        @PathVariable("tableauBordId") Long tableauBordId,
                        @PathVariable("id") Long id,
                        @Parameter(description = "Table format for indicateurs: 'pivot', 'flat', 'crud', 'create', 'both', or 'all'", example = "crud") @RequestParam(value = "tableFormat", defaultValue = "pivot", required = false) String tableFormat) {

                // Check if the user has access to the specified tableau de bord
                boolean hasAccess = hasResourceAccess(tableauBordId, "tableauBord", "lecture");
                boolean existsInTableauBord = tbDomaineService.existsInTableauBord(id, tableauBordId);

                if (!hasAccess || !existsInTableauBord) {
                        return createForbiddenResponse();
                }

                TBDomaineDetailDto domaineDetail;
                // if (tableFormat != null && !tableFormat.isEmpty()) {
                // // Use service method that adds table data
                // domaineDetail = domaineService.getDomaineWithTableData(id, tableFormat);
                // } else {
                // Use existing logic for backward compatibility
                TBDomaine tbDomaine = tbDomaineService.findById(id).orElseThrow(EntityNotFoundException::new);
                domaineDetail = tbDomaineDetailMapper.mapToDto(tbDomaine);
                // }

                return ResponseEntity.ok(RestResponse.<TBDomaineDetailDto>builder()
                                .data(domaineDetail)
                                .build());
        }

        @Override
        protected <DTO> DTO mapToDto(TBDomaine entity, Class<DTO> dtoClass) {
                if (dtoClass == TBDomaineDetailDto.class) {
                        return dtoClass.cast(tbDomaineDetailMapper.mapToDto(entity));
                } else if (dtoClass == TBDomaineDto.class) {
                        return dtoClass.cast(tbDomaineDtoMapper.mapToDto(entity));
                }
                throw new IllegalArgumentException("Unsupported DTO type: " + dtoClass.getName());
        }
}
