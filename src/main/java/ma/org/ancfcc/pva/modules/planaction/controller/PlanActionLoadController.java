package ma.org.ancfcc.pva.modules.planaction.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
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
import ma.org.ancfcc.pva.core.commun.rest.RestResponseUtil;
import ma.org.ancfcc.pva.core.commun.rest.queries.QueryParams;
import ma.org.ancfcc.pva.core.commun.rest.responses.RestResponse;
import ma.org.ancfcc.pva.modules.planaction.PlanAction;
import ma.org.ancfcc.pva.modules.planaction.dto.PlanActionDto;
import ma.org.ancfcc.pva.modules.planaction.dto.PlanActionDtoMapper;
import ma.org.ancfcc.pva.modules.planaction.dto.detail.PlanActionDetailDto;
import ma.org.ancfcc.pva.modules.planaction.dto.detail.PlanActionDetailMapper;
import ma.org.ancfcc.pva.modules.planaction.service.PlanActionService;

@RestController
@RequestMapping("/api/planactions")
@RequiredArgsConstructor
public class PlanActionLoadController {

        private static final String ENTITY_NAME = "planaction";

        private final PlanActionService planActionService;
        private final PlanActionDtoMapper planActionDtoMapper;
        private final PlanActionDetailMapper planActionDetailMapper;

        @Operation(summary = "Get all " + ENTITY_NAME + "s")
        @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Ok", content = {
                        @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PlanActionDto.class))) }),
                        @ApiResponse(responseCode = "404", description = ENTITY_NAME
                                        + " not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @GetMapping("")
        @PreAuthorize("hasAuthority('planaction:list')")
        public ResponseEntity<RestResponse<List<PlanActionDto>>> getPlanActions(
                        @RequestParam(value = "pageIndex", defaultValue = "0") int pageIndex,
                        @RequestParam(value = "pageSize", defaultValue = "-1") int pageSize,
                        @RequestParam(value = "sortField", defaultValue = "createdDate") String sortField,
                        @RequestParam(value = "sortDirection", defaultValue = "DESC") Direction direction,
                        @RequestParam(value = "filters", defaultValue = "") List<String> filters,
                        @RequestParam(value = "globalFilter", defaultValue = "") String globalFilter) {

                QueryParams requestParams = createQueryParams(pageIndex, pageSize, sortField, direction, filters,
                                globalFilter);

                Page<PlanAction> planActionPage = planActionService.getPlanActions(requestParams);
                List<PlanActionDto> dtos = planActionDtoMapper.mapToDto(planActionPage.getContent());
                QueryParams queryParams = adjustQueryParamsForAllRecords(requestParams, planActionPage);

                RestResponse<List<PlanActionDto>> restResponse = RestResponseUtil.buildRestResponse(dtos, queryParams);

                return ResponseEntity.ok(restResponse);
        }

        @Operation(summary = "Get " + ENTITY_NAME + " by id")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ok", content = {
                                        @Content(mediaType = "application/json", schema = @Schema(implementation = PlanActionDto.class)) }),
                        @ApiResponse(responseCode = "404", description = ENTITY_NAME
                                        + " not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @GetMapping("/{id}")
        @PreAuthorize("hasAuthority('planaction:read')")
        public ResponseEntity<RestResponse<PlanActionDetailDto>> getPlanAction(@PathVariable("id") Long id) {
                PlanAction planAction = planActionService.findById(id).orElseThrow(EntityNotFoundException::new);
                PlanActionDetailDto dto = planActionDetailMapper.mapToDto(planAction);
                RestResponse<PlanActionDetailDto> restResponse = RestResponseUtil.buildRestResponse(dto);
                return ResponseEntity.ok(restResponse);
        }

        private QueryParams createQueryParams(int pageIndex, int pageSize, String sortField, Direction direction,
                        List<String> filters, String globalFilter) {
                if (pageSize == -1) {
                        pageSize = Integer.MAX_VALUE; // Set pageSize to a large number to fetch all records
                }
                return new QueryParams(pageIndex, pageSize, sortField, direction, filters, globalFilter);
        }

        private QueryParams adjustQueryParamsForAllRecords(QueryParams requestParams, Page<PlanAction> planActionPage) {
                QueryParams queryParams = QueryParams.buildQueryParams(requestParams, planActionPage);
                if (requestParams.getPageSize() == Integer.MAX_VALUE) {
                        queryParams.setPageSize((int) queryParams.getTotalElements());
                }
                return queryParams;
        }

}
