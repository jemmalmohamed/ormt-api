package ma.org.ormt.modules.indicateurs.graphe.configuration.controllers.admin;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import ma.org.ormt.core.commun.base.controller.BaseController;
import ma.org.ormt.core.commun.rest.responses.RestResponse;
import ma.org.ormt.core.validators.groups.OnCreate;
import ma.org.ormt.core.validators.groups.OnUpdate;
import ma.org.ormt.modules.indicateurs.graphe.configuration.dtos.GrapheConfigurationDto;
import ma.org.ormt.modules.indicateurs.graphe.configuration.dtos.GrapheConfigurationDtoMapper;
import ma.org.ormt.modules.indicateurs.graphe.configuration.dtos.request.GrapheConfigurationRequestDto;
import ma.org.ormt.modules.indicateurs.graphe.configuration.models.GrapheConfiguration;
import ma.org.ormt.modules.indicateurs.graphe.configuration.services.GrapheConfigurationService;

@RestController
@RequestMapping(value = "/api/v1/admin/graphe-configuration")
@RequiredArgsConstructor
@Tag(name = "GrapheConfiguration", description = "GrapheConfiguration API")
public class GrapheConfigurationCrudController extends BaseController<GrapheConfiguration> {

        private static final String ENTITY_NAME = "grapGrapheConfiguration";

        private final GrapheConfigurationService grapGrapheConfigurationService;
        private final GrapheConfigurationDtoMapper grapGrapheConfigurationDtoMapper;

        @Operation(summary = "create " + ENTITY_NAME, responses = {
                        @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GrapheConfigurationDto.class))),
                        @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @PostMapping("")
        @PreAuthorize("hasAuthority('indicateur:create')")
        public ResponseEntity<RestResponse<GrapheConfigurationDto>> createGrapheConfiguration(
                        @Validated(OnCreate.class) @RequestBody GrapheConfigurationRequestDto requestDto) {
                GrapheConfiguration grapGrapheConfiguration = grapGrapheConfigurationService.create(requestDto);
                return buildResponseEntity(grapGrapheConfiguration, GrapheConfigurationDto.class, HttpStatus.CREATED);
        }

        @Operation(summary = "Update " + ENTITY_NAME, responses = {
                        @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GrapheConfigurationDto.class))),
                        @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @PutMapping("{id}")
        @PreAuthorize("hasAuthority('indicateur:edit')")
        public ResponseEntity<RestResponse<GrapheConfigurationDto>> updateGrapheConfiguration(@PathVariable Long id,
                        @Validated(OnUpdate.class) @RequestBody GrapheConfigurationRequestDto grapGrapheConfigurationRequestDto) {
                GrapheConfiguration grapGrapheConfiguration = grapGrapheConfigurationService.update(id,
                                grapGrapheConfigurationRequestDto);
                return buildResponseEntity(grapGrapheConfiguration, GrapheConfigurationDto.class, HttpStatus.OK);
        }

        @Operation(summary = "Delete " + ENTITY_NAME, responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/{id}")
        @PreAuthorize("hasAuthority('indicateur:delete')")
        public ResponseEntity<Void> deleteById(@PathVariable Long id) {
                return handleDelete(() -> grapGrapheConfigurationService.delete(id));
        }

        @Operation(summary = "Delete multiple " + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/bulk")
        @PreAuthorize("hasAuthority('indicateur:delete')")
        public ResponseEntity<RestResponse<List<Long>>> deleteMultiple(@RequestBody List<Long> ids) {
                try {
                        grapGrapheConfigurationService.deleteAllById(ids);
                        return buildResponseEntity(ids, HttpStatus.OK);
                } catch (DataIntegrityViolationException e) {
                        // Handle foreign key constraint violation
                        RestResponse<List<Long>> errorResponse = RestResponse.<List<Long>>builder()
                                        .success(false)
                                        .message("Suppression impossible, les données sont utilisées ailleurs")
                                        .data(ids)
                                        .build();
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
                } catch (Exception e) {
                        // Handle other exceptions
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }
                // return handleDelete(() -> grapGrapheConfigurationService.deleteAllById(ids));
        }

        @Operation(summary = "Delete all " + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/all")
        @PreAuthorize("hasAuthority('indicateur:delete')")
        public ResponseEntity<Void> deleteAll() {
                return handleDelete(grapGrapheConfigurationService::deleteAll);
        }

        @Operation(summary = "Delete all except specified IDs " + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/exclude")
        @PreAuthorize("hasAuthority('indicateur:delete')")
        public ResponseEntity<Void> deleteAllExcept(@RequestBody List<Long> ids) {
                return handleDelete(() -> grapGrapheConfigurationService.deleteAllExceptIds(ids));
        }

        @Operation(summary = "Delete by query parameters " + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/query")
        @PreAuthorize("hasAuthority('indicateur:delete')")
        public ResponseEntity<RestResponse<List<Long>>> deleteByQueryParams(
                        @RequestParam(value = "filters", defaultValue = "") List<String> filters,
                        @RequestParam(value = "globalFilter", defaultValue = "") String globalFilter) {
                List<Long> deletedIds = grapGrapheConfigurationService.deleteBySpecification(filters, globalFilter,
                                GrapheConfiguration.class);
                return buildResponseEntity(deletedIds, HttpStatus.OK);
        }

        @Operation(summary = "Delete by query parameters except ids" + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/query-exclude")
        @PreAuthorize("hasAuthority('indicateur:delete')")
        public ResponseEntity<RestResponse<List<Long>>> deleteByQueryParamsExceptIds(
                        @RequestBody List<Long> ids,
                        @RequestParam(value = "filters", defaultValue = "") List<String> filters,
                        @RequestParam(value = "globalFilter", defaultValue = "") String globalFilter) {
                List<Long> deletedIds = grapGrapheConfigurationService.deleteBySpecificationExceptIds(filters,
                                globalFilter,
                                GrapheConfiguration.class, ids);
                return buildResponseEntity(deletedIds, HttpStatus.OK);
        }

        // @Operation(summary = "Associate grapGrapheConfiguration with indicateur",
        // responses = {
        // @ApiResponse(responseCode = "204", description = "No content"),
        // @ApiResponse(responseCode = "404", description = "Not found", content =
        // @Content(mediaType = "ErrorResponse")),
        // @ApiResponse(responseCode = "403", description = "Permission denied", content
        // = @Content(mediaType = "ErrorResponse"))
        // })
        // @PostMapping("/associate")
        // @PreAuthorize("hasAuthority('indicateur:edit')")
        // public ResponseEntity<Void> associateWithIndicateur(@RequestBody
        // IndicateurGrapheConfigurationDto associationDto) {
        // grapGrapheConfigurationService.associateWithIndicateur(associationDto.getIdGrapheConfiguration(),
        // associationDto.getIdIndicateur());
        // return ResponseEntity.noContent().build();
        // }

        // @Operation(summary = "Dissociate grapGrapheConfiguration from indicateur",
        // responses = {
        // @ApiResponse(responseCode = "204", description = "No content"),
        // @ApiResponse(responseCode = "404", description = "Not found", content =
        // @Content(mediaType = "ErrorResponse")),
        // @ApiResponse(responseCode = "403", description = "Permission denied", content
        // = @Content(mediaType = "ErrorResponse"))
        // })
        // @DeleteMapping("/dissociate")
        // @PreAuthorize("hasAuthority('indicateur:edit')")
        // public ResponseEntity<Void> dissociateFromIndicateur(@RequestBody
        // IndicateurGrapheConfigurationDto associationDto) {
        // grapGrapheConfigurationService.dissociateFromIndicateur(associationDto.getIdGrapheConfiguration(),
        // associationDto.getIdIndicateur());
        // return ResponseEntity.noContent().build();
        // }

        @Override
        protected <DTO> DTO mapToDto(GrapheConfiguration entity, Class<DTO> dtoClass) {
                return dtoClass.cast(grapGrapheConfigurationDtoMapper.mapToDto(entity));
        }
}