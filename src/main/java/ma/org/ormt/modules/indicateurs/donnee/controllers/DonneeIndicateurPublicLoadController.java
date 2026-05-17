package ma.org.ormt.modules.indicateurs.donnee.controllers;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import ma.org.ormt.core.commun.base.controller.BaseController;
import ma.org.ormt.core.commun.rest.queries.QueryParams;
import ma.org.ormt.core.commun.rest.responses.RestResponse;
import ma.org.ormt.modules.indicateurs.donnee.dtos.DonneeIndicateurDto;
import ma.org.ormt.modules.indicateurs.donnee.dtos.DonneeIndicateurDtoMapper;
import ma.org.ormt.modules.indicateurs.donnee.dtos.details.DonneeIndicateurDetailsDto;
import ma.org.ormt.modules.indicateurs.donnee.dtos.details.DonneeIndicateurDetailsDtoMapper;
import ma.org.ormt.modules.indicateurs.donnee.models.DonneeIndicateur;
import ma.org.ormt.modules.indicateurs.donnee.services.DonneeIndicateurService;

@RestController
@RequestMapping("api/v1/public/indicateurs")
@RequiredArgsConstructor
public class DonneeIndicateurPublicLoadController extends BaseController<DonneeIndicateur> {

        private static final String ENTITY_NAME = "donneeIndicateur";

        private final DonneeIndicateurService donneeIndicateurService;
        private final DonneeIndicateurDtoMapper donneeIndicateurDtoMapper;
        private final DonneeIndicateurDetailsDtoMapper donneeIndicateurDetailMapper;

        @Operation(summary = "Get all " + ENTITY_NAME
                        + "s", description = "Get all donnee indicateur for an indicateur with optional table data. " +
                                        "Use tableFormat parameter: 'pivot' for pivot table, 'flat' for flat table, 'crud' for CRUD operations, "
                                        +
                                        "'create' for create template, 'both' for pivot+flat, 'all' for all formats")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ok - Returns List<DonneeIndicateurDto> when tableFormat is not specified, or List<DonneeIndicateurDetailsDto> when tableFormat is specified", content = {
                                        @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = DonneeIndicateurDto.class))),
                                        @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = DonneeIndicateurDetailsDto.class))) }),
                        @ApiResponse(responseCode = "404", description = ENTITY_NAME
                                        + " not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @GetMapping("/{indicateurId}/donnees")
        public ResponseEntity<RestResponse<?>> getDonneeIndicateur(
                        @PathVariable("indicateurId") Long indicateurId,
                        @RequestParam(value = "pageIndex", defaultValue = "0") int pageIndex,
                        @RequestParam(value = "pageSize", defaultValue = "-1") int pageSize,
                        @RequestParam(value = "sortField", defaultValue = "createdDate") String sortField,
                        @RequestParam(value = "sortDirection", defaultValue = "DESC") Direction direction,
                        @RequestParam(value = "filters", defaultValue = "") List<String> filters,
                        @RequestParam(value = "globalFilter", defaultValue = "") String globalFilter,
                        @Parameter(description = "Table format: 'pivot', 'flat', 'crud', 'create', 'both', or 'all'", example = "crud") @RequestParam(value = "tableFormat", required = false) String tableFormat) {

                QueryParams requestParams = buildQueryParams(pageIndex, pageSize, sortField, direction, filters,
                                globalFilter);

                Page<DonneeIndicateur> donneeIndicateurPage = donneeIndicateurService.getEntityListByIndicateurId(
                                indicateurId,
                                requestParams);

                QueryParams queryParams = adjustQueryParamsToGetAllRecords(requestParams, donneeIndicateurPage);

                // If tableFormat is requested, convert to details DTOs with table data
                // if (tableFormat != null && !tableFormat.isEmpty()) {
                // List<DonneeIndicateurDetailsDto> detailsList =
                // donneeIndicateurPage.getContent().stream()
                // .map(donnee -> donneeIndicateurService.getDonneeIndicateurWithTableData(
                // indicateurId, donnee.getId(), tableFormat))
                // .toList();

                // return
                // ResponseEntity.ok(RestResponse.<List<DonneeIndicateurDetailsDto>>builder()
                // .data(detailsList)
                // .queryParams(queryParams)
                // .build());
                // } else {
                // Use existing logic for backward compatibility
                ResponseEntity<RestResponse<List<DonneeIndicateurDto>>> response = buildResponseEntity(
                                donneeIndicateurPage.getContent(), DonneeIndicateurDto.class, queryParams,
                                HttpStatus.OK, true);
                return ResponseEntity.ok(RestResponse.builder()
                                .data(response.getBody().getData())
                                .queryParams(queryParams)
                                .build());
                // }
        }

        @Operation(summary = "Get " + ENTITY_NAME
                        + " by id", description = "Get donnee indicateur details with optional table data. " +
                                        "Use tableFormat parameter: 'pivot' for pivot table, 'flat' for flat table, 'crud' for CRUD operations, "
                                        +
                                        "'create' for create template, 'both' for pivot+flat, 'all' for all formats")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ok", content = {
                                        @Content(mediaType = "application/json", schema = @Schema(implementation = DonneeIndicateurDetailsDto.class)) }),
                        @ApiResponse(responseCode = "404", description = ENTITY_NAME
                                        + " not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @GetMapping("/{indicateurId}/donnees/{id}")

        public ResponseEntity<RestResponse<DonneeIndicateurDetailsDto>> getDonneeIndicateur(
                        @PathVariable("indicateurId") Long indicateurId,
                        @PathVariable("id") Long id,
                        @Parameter(description = "Table format: 'pivot', 'flat', 'crud', 'create', 'both', or 'all'", example = "crud") @RequestParam(value = "tableFormat", required = false) String tableFormat) {

                DonneeIndicateurDetailsDto donneeDetail;

                // Use existing logic for backward compatibility
                DonneeIndicateur donneeIndicateur = donneeIndicateurService.findById(id)
                                .orElseThrow(EntityNotFoundException::new);
                donneeDetail = donneeIndicateurDetailMapper.mapToDto(donneeIndicateur);
                // }

                return ResponseEntity.ok(RestResponse.<DonneeIndicateurDetailsDto>builder()
                                .data(donneeDetail)
                                .build());
        }

        @Override
        protected <DTO> DTO mapToDto(DonneeIndicateur entity, Class<DTO> dtoClass) {
                if (dtoClass == DonneeIndicateurDetailsDto.class) {
                        return dtoClass.cast(donneeIndicateurDetailMapper.mapToDto(entity));
                } else if (dtoClass == DonneeIndicateurDto.class) {
                        return dtoClass.cast(donneeIndicateurDtoMapper.mapToDto(entity));
                }
                throw new IllegalArgumentException("Unsupported DTO type: " + dtoClass.getName());
        }

}
