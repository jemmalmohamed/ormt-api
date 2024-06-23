package ma.org.ancfcc.pva.modules.avion.controller;

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
import ma.org.ancfcc.pva.modules.avion.Avion;
import ma.org.ancfcc.pva.modules.avion.dto.AvionDto;
import ma.org.ancfcc.pva.modules.avion.dto.AvionDtoMapper;
import ma.org.ancfcc.pva.modules.avion.dto.request.AvionRequestDto;
import ma.org.ancfcc.pva.modules.avion.service.AvionService;

@RestController
@RequestMapping(value = "/api/v1/avions")
@RequiredArgsConstructor
@Tag(name = "Avion", description = "Avion API")
public class AvionCrudController extends BaseController<Avion> {

        private static final String ENTITY_NAME = "avion";

        private final AvionService avionService;

        private final AvionDtoMapper avionDtoMapper;

        @Operation(summary = "create " + ENTITY_NAME, responses = {
                        @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AvionDto.class))),
                        @ApiResponse(responseCode = "402", description = "Unprocessable entity", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @PostMapping("")
        @PreAuthorize("hasAuthority('avion:create')")
        public ResponseEntity<RestResponse<AvionDto>> createAvion(
                        @Validated(OnCreate.class) @RequestBody AvionRequestDto requestDto) {
                Avion avion = avionService.create(requestDto);
                return buildResponseEntity(avion, AvionDto.class, HttpStatus.CREATED);

        }

        // *********** UPDATE OPERATIONS ***********

        @Operation(summary = "Update " + ENTITY_NAME, responses = {
                        @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AvionDto.class))),
                        @ApiResponse(responseCode = "402", description = "Unprocessable entity", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @PutMapping("{id}")
        @PreAuthorize("hasAuthority('avion:update')")
        public ResponseEntity<RestResponse<AvionDto>> updateAvion(@PathVariable UUID id,
                        @Validated(OnUpdate.class) @RequestBody AvionRequestDto avionRequestDto) {
                Avion avion = avionService.update(id, avionRequestDto);
                return buildResponseEntity(avion, AvionDto.class, HttpStatus.OK);
        }

        @Operation(summary = "Delete " + ENTITY_NAME, responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/{id}")
        @PreAuthorize("hasAuthority('avion:delete')")
        public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
                return handleDelete(() -> avionService.delete(id));
        }

        @Operation(summary = "Delete multiple " + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/bulk")
        @PreAuthorize("hasAuthority('avion:delete')")
        public ResponseEntity<Void> deleteMultiple(@RequestBody List<UUID> ids) {
                return handleDelete(() -> avionService.deleteAllById(ids));
        }

        @Operation(summary = "Delete all " + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/all")
        @PreAuthorize("hasAuthority('avion:delete')")
        public ResponseEntity<Void> deleteAll() {
                return handleDelete(avionService::deleteAll);
        }

        @Operation(summary = "Delete all except specified IDs " + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/exclude")
        @PreAuthorize("hasAuthority('avion:delete')")
        public ResponseEntity<Void> deleteAllExcept(@RequestBody List<UUID> ids) {
                return handleDelete(() -> avionService.deleteAllExceptIds(ids));
        }

        @Operation(summary = "Delete by query parameters " + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/query")
        @PreAuthorize("hasAuthority('avion:delete')")
        public ResponseEntity<RestResponse<List<UUID>>> deleteByQueryParams(
                        @RequestParam(value = "filters", defaultValue = "") List<String> filters,
                        @RequestParam(value = "globalFilter", defaultValue = "") String globalFilter) {

                List<UUID> deletedIds = avionService.deleteBySpecification(filters, globalFilter,
                                Avion.class);
                return buildResponseEntity(deletedIds, HttpStatus.OK);

        }

        @Operation(summary = "Delete by query parameters  except ids" + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/query-exclude")
        @PreAuthorize("hasAuthority('avion:delete')")
        public ResponseEntity<RestResponse<List<UUID>>> deleteByQueryParamsExceptIds(
                        @RequestBody List<UUID> ids,
                        @RequestParam(value = "filters", defaultValue = "") List<String> filters,
                        @RequestParam(value = "globalFilter", defaultValue = "") String globalFilter) {

                List<UUID> deletedIds = avionService.deleteBySpecificationExceptIds(filters, globalFilter,
                                Avion.class, ids);

                return buildResponseEntity(deletedIds, HttpStatus.OK);

        }

        @Override
        protected <DTO> DTO mapToDto(Avion entity, Class<DTO> dtoClass) {
                return dtoClass.cast(avionDtoMapper.mapToDto(entity));
        }

}