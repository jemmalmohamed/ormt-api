package ma.org.ormt.modules.indicateurs.dimension.controllers.admin;

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
import ma.org.ormt.core.exceptions.handlers.DependencyException;
import ma.org.ormt.core.validators.groups.OnCreate;
import ma.org.ormt.core.validators.groups.OnUpdate;
import ma.org.ormt.modules.indicateurs.dimension.dtos.DimensionDto;
import ma.org.ormt.modules.indicateurs.dimension.dtos.DimensionDtoMapper;
import ma.org.ormt.modules.indicateurs.dimension.dtos.request.DimensionRequestDto;
import ma.org.ormt.modules.indicateurs.dimension.models.Dimension;
import ma.org.ormt.modules.indicateurs.dimension.services.DimensionService;

@RestController
@RequestMapping(value = "/api/v1/admin/dimensions")
@RequiredArgsConstructor
@Tag(name = "Dimension", description = "Dimension API")
public class DimensionAdminCrudController extends BaseController<Dimension> {

        private static final String ENTITY_NAME = "dimension";

        private final DimensionService dimensionService;
        private final DimensionDtoMapper dimensionDtoMapper;

        @Operation(summary = "create " + ENTITY_NAME, responses = {
                        @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DimensionDto.class))),
                        @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @PostMapping("")
        @PreAuthorize("hasAuthority('dimension:create')")
        public ResponseEntity<RestResponse<DimensionDto>> createDimension(
                        @Validated(OnCreate.class) @RequestBody DimensionRequestDto requestDto) {
                Dimension dimension = dimensionService.create(requestDto);
                return buildResponseEntity(dimension, DimensionDto.class, HttpStatus.CREATED);
        }

        @Operation(summary = "Update " + ENTITY_NAME, responses = {
                        @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DimensionDto.class))),
                        @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @PutMapping("{id}")
        @PreAuthorize("hasAuthority('dimension:edit')")
        public ResponseEntity<RestResponse<DimensionDto>> updateDimension(@PathVariable Long id,
                        @Validated(OnUpdate.class) @RequestBody DimensionRequestDto dimensionRequestDto) {
                Dimension dimension = dimensionService.update(id, dimensionRequestDto);
                return buildResponseEntity(dimension, DimensionDto.class, HttpStatus.OK);
        }

        @Operation(summary = "Delete " + ENTITY_NAME, responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/{id}")
        @PreAuthorize("hasAuthority('dimension:delete')")
        public ResponseEntity<Void> deleteById(@PathVariable Long id) {
                return handleDelete(() -> dimensionService.delete(id));
        }

        @Operation(summary = "Delete multiple " + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/bulk")
        @PreAuthorize("hasAuthority('dimension:delete')")
        public ResponseEntity<RestResponse<List<Long>>> deleteMultiple(@RequestBody List<Long> ids) {
                try {
                        dimensionService.deleteAllById(ids);
                        return buildResponseEntity(ids, HttpStatus.OK);
                }

                catch (DependencyException e) {

                        RestResponse<List<Long>> errorResponse = RestResponse.<List<Long>>builder()
                                        .success(false)
                                        .message(e.getMessage())
                                        .data(ids)
                                        .build();
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);

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
                // return handleDelete(() -> dimensionService.deleteAllById(ids));
        }

        @Operation(summary = "Delete all " + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/all")
        @PreAuthorize("hasAuthority('dimension:delete')")
        public ResponseEntity<Void> deleteAll() {
                return handleDelete(dimensionService::deleteAll);
        }

        @Operation(summary = "Delete all except specified IDs " + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/exclude")
        @PreAuthorize("hasAuthority('dimension:delete')")
        public ResponseEntity<Void> deleteAllExcept(@RequestBody List<Long> ids) {
                return handleDelete(() -> dimensionService.deleteAllExceptIds(ids));
        }

        @Operation(summary = "Delete by query parameters " + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/query")
        @PreAuthorize("hasAuthority('dimension:delete')")
        public ResponseEntity<RestResponse<List<Long>>> deleteByQueryParams(
                        @RequestParam(value = "filters", defaultValue = "") List<String> filters,
                        @RequestParam(value = "globalFilter", defaultValue = "") String globalFilter) {
                List<Long> deletedIds = dimensionService.deleteBySpecification(filters, globalFilter,
                                Dimension.class);
                return buildResponseEntity(deletedIds, HttpStatus.OK);
        }

        @Operation(summary = "Delete by query parameters except ids" + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/query-exclude")
        @PreAuthorize("hasAuthority('dimension:delete')")
        public ResponseEntity<RestResponse<List<Long>>> deleteByQueryParamsExceptIds(
                        @RequestBody List<Long> ids,
                        @RequestParam(value = "filters", defaultValue = "") List<String> filters,
                        @RequestParam(value = "globalFilter", defaultValue = "") String globalFilter) {
                List<Long> deletedIds = dimensionService.deleteBySpecificationExceptIds(filters, globalFilter,
                                Dimension.class, ids);
                return buildResponseEntity(deletedIds, HttpStatus.OK);
        }

        @Override
        protected <DTO> DTO mapToDto(Dimension entity, Class<DTO> dtoClass) {
                return dtoClass.cast(dimensionDtoMapper.mapToDto(entity));
        }
}