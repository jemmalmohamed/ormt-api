package ma.org.ormt.modules.indicateurs.graphe.configuration.controllers.admin;

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
import ma.org.ormt.modules.indicateurs.graphe.configuration.dtos.GrapheConfigurationDto;
import ma.org.ormt.modules.indicateurs.graphe.configuration.dtos.GrapheConfigurationDtoMapper;
import ma.org.ormt.modules.indicateurs.graphe.configuration.dtos.details.GrapheConfigurationDetailsDto;
import ma.org.ormt.modules.indicateurs.graphe.configuration.dtos.details.GrapheConfigurationDetailsDtoMapper;
import ma.org.ormt.modules.indicateurs.graphe.configuration.models.GrapheConfiguration;
import ma.org.ormt.modules.indicateurs.graphe.configuration.services.GrapheConfigurationService;

@RestController
@RequestMapping("api/v1/admin/graphe-configuration")
@RequiredArgsConstructor
public class GrapheConfigurationAdminLoadController extends BaseController<GrapheConfiguration> {

        private static final String ENTITY_NAME = "grapheConfiguration";

        private final GrapheConfigurationService grapheConfigurationService;
        private final GrapheConfigurationDtoMapper grapheConfigurationDtoMapper;
        private final GrapheConfigurationDetailsDtoMapper grapheConfigurationDetailMapper;

        @Operation(summary = "Get all " + ENTITY_NAME + "s")
        @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Ok", content = {
                        @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = GrapheConfigurationDto.class))) }),
                        @ApiResponse(responseCode = "404", description = ENTITY_NAME
                                        + " not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @GetMapping("")
        @PreAuthorize("hasAuthority('indicateur:list')")
        public ResponseEntity<RestResponse<List<GrapheConfigurationDto>>> getGrapheConfigurations(
                        @RequestParam(value = "pageIndex", defaultValue = "0") int pageIndex,
                        @RequestParam(value = "pageSize", defaultValue = "-1") int pageSize,
                        @RequestParam(value = "sortField", defaultValue = "createdDate") String sortField,
                        @RequestParam(value = "sortDirection", defaultValue = "DESC") Direction direction,
                        @RequestParam(value = "filters", defaultValue = "") List<String> filters,
                        @RequestParam(value = "globalFilter", defaultValue = "") String globalFilter) {

                QueryParams requestParams = buildQueryParams(pageIndex, pageSize, sortField, direction, filters,
                                globalFilter);

                Page<GrapheConfiguration> grapheConfigurationPage = grapheConfigurationService
                                .getEntityList(requestParams);

                QueryParams queryParams = adjustQueryParamsToGetAllRecords(requestParams, grapheConfigurationPage);

                return buildResponseEntity(grapheConfigurationPage.getContent(), GrapheConfigurationDto.class,
                                queryParams, HttpStatus.OK, true);
        }

        @Operation(summary = "Get " + ENTITY_NAME + " by id")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ok", content = {
                                        @Content(mediaType = "application/json", schema = @Schema(implementation = GrapheConfigurationDto.class)) }),
                        @ApiResponse(responseCode = "404", description = ENTITY_NAME
                                        + " not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @GetMapping("/{id}")
        @PreAuthorize("hasAuthority('indicateur:read')")
        public ResponseEntity<RestResponse<GrapheConfigurationDetailsDto>> getGrapheConfiguration(
                        @PathVariable("id") Long id) {
                GrapheConfiguration grapheConfiguration = grapheConfigurationService.findById(id)
                                .orElseThrow(EntityNotFoundException::new);
                return buildResponseEntity(grapheConfiguration, GrapheConfigurationDetailsDto.class, HttpStatus.OK);
        }

        @Override
        protected <DTO> DTO mapToDto(GrapheConfiguration entity, Class<DTO> dtoClass) {
                if (dtoClass == GrapheConfigurationDetailsDto.class) {
                        return dtoClass.cast(grapheConfigurationDetailMapper.mapToDto(entity));
                } else if (dtoClass == GrapheConfigurationDto.class) {
                        return dtoClass.cast(grapheConfigurationDtoMapper.mapToDto(entity));
                }
                throw new IllegalArgumentException("Unsupported DTO type: " + dtoClass.getName());
        }
}