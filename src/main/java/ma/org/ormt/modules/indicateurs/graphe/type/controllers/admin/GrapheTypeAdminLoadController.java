package ma.org.ormt.modules.indicateurs.graphe.type.controllers.admin;

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
import ma.org.ormt.modules.indicateurs.graphe.type.dtos.GrapheTypeDto;
import ma.org.ormt.modules.indicateurs.graphe.type.dtos.GrapheTypeDtoMapper;
import ma.org.ormt.modules.indicateurs.graphe.type.dtos.details.GrapheTypeDetailsDto;
import ma.org.ormt.modules.indicateurs.graphe.type.dtos.details.GrapheTypeDetailsDtoMapper;
import ma.org.ormt.modules.indicateurs.graphe.type.models.GrapheType;
import ma.org.ormt.modules.indicateurs.graphe.type.services.GrapheTypeService;

@RestController
@RequestMapping("api/v1/admin/graphetypes")
@RequiredArgsConstructor
public class GrapheTypeAdminLoadController extends BaseController<GrapheType> {

        private static final String ENTITY_NAME = "graphetype";

        private final GrapheTypeService graphetypeService;
        private final GrapheTypeDtoMapper graphetypeDtoMapper;
        private final GrapheTypeDetailsDtoMapper graphetypeDetailMapper;

        @Operation(summary = "Get all " + ENTITY_NAME + "s")
        @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Ok", content = {
                        @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = GrapheTypeDto.class))) }),
                        @ApiResponse(responseCode = "404", description = ENTITY_NAME
                                        + " not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @GetMapping("")
        // @PreAuthorize("hasAuthority('indicateur:list')")
        public ResponseEntity<RestResponse<List<GrapheTypeDto>>> getGrapheTypes(
                        @RequestParam(value = "pageIndex", defaultValue = "0") int pageIndex,
                        @RequestParam(value = "pageSize", defaultValue = "-1") int pageSize,
                        @RequestParam(value = "sortField", defaultValue = "createdDate") String sortField,
                        @RequestParam(value = "sortDirection", defaultValue = "DESC") Direction direction,
                        @RequestParam(value = "filters", defaultValue = "") List<String> filters,
                        @RequestParam(value = "globalFilter", defaultValue = "") String globalFilter) {

                QueryParams requestParams = buildQueryParams(pageIndex, pageSize, sortField, direction, filters,
                                globalFilter);

                Page<GrapheType> graphetypePage = graphetypeService.getEntityList(requestParams);

                QueryParams queryParams = adjustQueryParamsToGetAllRecords(requestParams, graphetypePage);

                return buildResponseEntity(graphetypePage.getContent(), GrapheTypeDto.class, queryParams,
                                HttpStatus.OK, true);
        }

        @Operation(summary = "Get " + ENTITY_NAME + " by id")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ok", content = {
                                        @Content(mediaType = "application/json", schema = @Schema(implementation = GrapheTypeDto.class)) }),
                        @ApiResponse(responseCode = "404", description = ENTITY_NAME
                                        + " not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @GetMapping("/{id}")
        // @PreAuthorize("hasAuthority('indicateur:read')")
        public ResponseEntity<RestResponse<GrapheTypeDetailsDto>> getGrapheType(@PathVariable("id") Long id) {
                GrapheType graphetype = graphetypeService.findById(id).orElseThrow(EntityNotFoundException::new);
                return buildResponseEntity(graphetype, GrapheTypeDetailsDto.class, HttpStatus.OK);
        }

        @Override
        protected <DTO> DTO mapToDto(GrapheType entity, Class<DTO> dtoClass) {
                if (dtoClass == GrapheTypeDetailsDto.class) {
                        return dtoClass.cast(graphetypeDetailMapper.mapToDto(entity));
                } else if (dtoClass == GrapheTypeDto.class) {
                        return dtoClass.cast(graphetypeDtoMapper.mapToDto(entity));
                }
                throw new IllegalArgumentException("Unsupported DTO type: " + dtoClass.getName());
        }
}