package ma.org.ancfcc.pva.modules.basemap.controller;

import java.util.List;
import java.util.UUID;

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
import ma.org.ancfcc.pva.core.commun.base.controller.BaseController;
import ma.org.ancfcc.pva.core.commun.rest.responses.RestResponse;
import ma.org.ancfcc.pva.core.validators.groups.OnCreate;
import ma.org.ancfcc.pva.core.validators.groups.OnUpdate;
import ma.org.ancfcc.pva.modules.basemap.Basemap;
import ma.org.ancfcc.pva.modules.basemap.dto.BasemapDto;
import ma.org.ancfcc.pva.modules.basemap.dto.BasemapDtoMapper;
import ma.org.ancfcc.pva.modules.basemap.dto.request.BasemapRequestDto;
import ma.org.ancfcc.pva.modules.basemap.service.BasemapService;

@RestController
@RequestMapping(value = "/api/v1/basemaps")
@RequiredArgsConstructor
@Tag(name = "Basemap", description = "Basemap API")
public class BasemapCrudController extends BaseController<Basemap> {

        private static final String ENTITY_NAME = "basemap";

        private final BasemapService basemapService;

        private final BasemapDtoMapper basemapDtoMapper;

        @Operation(summary = "create " + ENTITY_NAME, responses = {
                        @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BasemapDto.class))),
                        @ApiResponse(responseCode = "402", description = "Unprocessable entity", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @PostMapping("")
        @PreAuthorize("hasAuthority('basemap:create')")
        public ResponseEntity<RestResponse<BasemapDto>> createBasemap(
                        @Validated(OnCreate.class) @RequestBody BasemapRequestDto requestDto) {
                Basemap basemap = basemapService.create(requestDto);
                return buildResponseEntity(basemap, BasemapDto.class, HttpStatus.CREATED);

        }

        // *********** UPDATE OPERATIONS ***********

        @Operation(summary = "Update " + ENTITY_NAME, responses = {
                        @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BasemapDto.class))),
                        @ApiResponse(responseCode = "402", description = "Unprocessable entity", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @PutMapping("{id}")
        @PreAuthorize("hasAuthority('basemap:update')")
        public ResponseEntity<RestResponse<BasemapDto>> updateBasemap(@PathVariable UUID id,
                        @Validated(OnUpdate.class) @RequestBody BasemapRequestDto basemapRequestDto) {
                Basemap basemap = basemapService.update(id, basemapRequestDto);
                return buildResponseEntity(basemap, BasemapDto.class, HttpStatus.OK);
        }

        @Operation(summary = "Delete " + ENTITY_NAME, responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/{id}")
        @PreAuthorize("hasAuthority('basemap:delete')")
        public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
                return handleDelete(() -> basemapService.delete(id));
        }

        @Operation(summary = "Delete multiple " + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/bulk")
        @PreAuthorize("hasAuthority('basemap:delete')")
        public ResponseEntity<Void> deleteMultiple(@RequestBody List<UUID> ids) {
                return handleDelete(() -> basemapService.deleteAllById(ids));
        }

        @Operation(summary = "Delete all " + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/all")
        @PreAuthorize("hasAuthority('basemap:delete')")
        public ResponseEntity<Void> deleteAll() {
                return handleDelete(basemapService::deleteAll);
        }

        @Operation(summary = "Delete all except specified IDs " + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/exclude")
        @PreAuthorize("hasAuthority('basemap:delete')")
        public ResponseEntity<Void> deleteAllExcept(@RequestBody List<UUID> ids) {
                return handleDelete(() -> basemapService.deleteAllExceptIds(ids));
        }

        @Operation(summary = "Delete by query parameters " + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/query")
        @PreAuthorize("hasAuthority('basemap:delete')")
        public ResponseEntity<RestResponse<List<UUID>>> deleteByQueryParams(
                        @RequestParam(value = "filters", defaultValue = "") List<String> filters,
                        @RequestParam(value = "globalFilter", defaultValue = "") String globalFilter) {

                List<UUID> deletedIds = basemapService.deleteBySpecification(filters, globalFilter,
                                Basemap.class);
                return buildResponseEntity(deletedIds, HttpStatus.OK);

        }

        @Operation(summary = "Delete by query parameters  except ids" + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/query-exclude")
        @PreAuthorize("hasAuthority('basemap:delete')")
        public ResponseEntity<RestResponse<List<UUID>>> deleteByQueryParamsExceptIds(
                        @RequestBody List<UUID> ids,
                        @RequestParam(value = "filters", defaultValue = "") List<String> filters,
                        @RequestParam(value = "globalFilter", defaultValue = "") String globalFilter) {

                List<UUID> deletedIds = basemapService.deleteBySpecificationExceptIds(filters, globalFilter,
                                Basemap.class, ids);

                return buildResponseEntity(deletedIds, HttpStatus.OK);

        }

        @Override
        protected <DTO> DTO mapToDto(Basemap entity, Class<DTO> dtoClass) {
                return dtoClass.cast(basemapDtoMapper.mapToDto(entity));
        }

}