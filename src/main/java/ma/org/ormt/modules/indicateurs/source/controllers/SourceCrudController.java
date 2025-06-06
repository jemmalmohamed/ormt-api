package ma.org.ormt.modules.indicateurs.source.controllers;

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
import ma.org.ormt.modules.indicateurs.source.dtos.SourceDto;
import ma.org.ormt.modules.indicateurs.source.dtos.SourceDtoMapper;
import ma.org.ormt.modules.indicateurs.source.dtos.request.SourceRequestDto;
import ma.org.ormt.modules.indicateurs.source.models.Source;
import ma.org.ormt.modules.indicateurs.source.services.SourceService;

@RestController
@RequestMapping(value = "/api/v1/admin/sources")
@RequiredArgsConstructor
@Tag(name = "Source", description = "Source API")
public class SourceCrudController extends BaseController<Source> {

        private static final String ENTITY_NAME = "source";

        private final SourceService sourceService;
        private final SourceDtoMapper sourceDtoMapper;

        @Operation(summary = "create " + ENTITY_NAME, responses = {
                        @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SourceDto.class))),
                        @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @PostMapping("")
        @PreAuthorize("hasAuthority('datasource:create')")
        public ResponseEntity<RestResponse<SourceDto>> createSource(
                        @Validated(OnCreate.class) @RequestBody SourceRequestDto requestDto) {
                Source source = sourceService.create(requestDto);
                return buildResponseEntity(source, SourceDto.class, HttpStatus.CREATED);
        }

        @Operation(summary = "Update " + ENTITY_NAME, responses = {
                        @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SourceDto.class))),
                        @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @PutMapping("{id}")
        @PreAuthorize("hasAuthority('datasource:edit')")
        public ResponseEntity<RestResponse<SourceDto>> updateSource(@PathVariable Long id,
                        @Validated(OnUpdate.class) @RequestBody SourceRequestDto sourceRequestDto) {
                Source source = sourceService.update(id, sourceRequestDto);
                return buildResponseEntity(source, SourceDto.class, HttpStatus.OK);
        }

        @Operation(summary = "Delete " + ENTITY_NAME, responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/{id}")
        @PreAuthorize("hasAuthority('datasource:delete')")
        public ResponseEntity<Void> deleteById(@PathVariable Long id) {
                return handleDelete(() -> sourceService.delete(id));
        }

        @Operation(summary = "Delete multiple " + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/bulk")
        @PreAuthorize("hasAuthority('datasource:delete')")
        public ResponseEntity<RestResponse<List<Long>>> deleteMultiple(@RequestBody List<Long> ids) {
                try {
                        sourceService.deleteAllById(ids);
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
                // return handleDelete(() -> sourceService.deleteAllById(ids));
        }

        @Operation(summary = "Delete all " + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/all")
        @PreAuthorize("hasAuthority('datasource:delete')")
        public ResponseEntity<Void> deleteAll() {
                return handleDelete(sourceService::deleteAll);
        }

        @Operation(summary = "Delete all except specified IDs " + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/exclude")
        @PreAuthorize("hasAuthority('datasource:delete')")
        public ResponseEntity<Void> deleteAllExcept(@RequestBody List<Long> ids) {
                return handleDelete(() -> sourceService.deleteAllExceptIds(ids));
        }

        @Operation(summary = "Delete by query parameters " + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/query")
        @PreAuthorize("hasAuthority('datasource:delete')")
        public ResponseEntity<RestResponse<List<Long>>> deleteByQueryParams(
                        @RequestParam(value = "filters", defaultValue = "") List<String> filters,
                        @RequestParam(value = "globalFilter", defaultValue = "") String globalFilter) {
                List<Long> deletedIds = sourceService.deleteBySpecification(filters, globalFilter,
                                Source.class);
                return buildResponseEntity(deletedIds, HttpStatus.OK);
        }

        @Operation(summary = "Delete by query parameters except ids" + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/query-exclude")
        @PreAuthorize("hasAuthority('datasource:delete')")
        public ResponseEntity<RestResponse<List<Long>>> deleteByQueryParamsExceptIds(
                        @RequestBody List<Long> ids,
                        @RequestParam(value = "filters", defaultValue = "") List<String> filters,
                        @RequestParam(value = "globalFilter", defaultValue = "") String globalFilter) {
                List<Long> deletedIds = sourceService.deleteBySpecificationExceptIds(filters, globalFilter,
                                Source.class, ids);
                return buildResponseEntity(deletedIds, HttpStatus.OK);
        }

        // @Operation(summary = "Associate source with indicateur", responses = {
        // @ApiResponse(responseCode = "204", description = "No content"),
        // @ApiResponse(responseCode = "404", description = "Not found", content =
        // @Content(mediaType = "ErrorResponse")),
        // @ApiResponse(responseCode = "403", description = "Permission denied", content
        // = @Content(mediaType = "ErrorResponse"))
        // })
        // @PostMapping("/associate")
        // @PreAuthorize("hasAuthority('datasource:edit')")
        // public ResponseEntity<Void> associateWithIndicateur(@RequestBody
        // IndicateurSourceDto associationDto) {
        // sourceService.associateWithIndicateur(associationDto.getIdSource(),
        // associationDto.getIdIndicateur());
        // return ResponseEntity.noContent().build();
        // }

        // @Operation(summary = "Dissociate source from indicateur", responses = {
        // @ApiResponse(responseCode = "204", description = "No content"),
        // @ApiResponse(responseCode = "404", description = "Not found", content =
        // @Content(mediaType = "ErrorResponse")),
        // @ApiResponse(responseCode = "403", description = "Permission denied", content
        // = @Content(mediaType = "ErrorResponse"))
        // })
        // @DeleteMapping("/dissociate")
        // @PreAuthorize("hasAuthority('datasource:edit')")
        // public ResponseEntity<Void> dissociateFromIndicateur(@RequestBody
        // IndicateurSourceDto associationDto) {
        // sourceService.dissociateFromIndicateur(associationDto.getIdSource(),
        // associationDto.getIdIndicateur());
        // return ResponseEntity.noContent().build();
        // }

        @Override
        protected <DTO> DTO mapToDto(Source entity, Class<DTO> dtoClass) {
                return dtoClass.cast(sourceDtoMapper.mapToDto(entity));
        }
}