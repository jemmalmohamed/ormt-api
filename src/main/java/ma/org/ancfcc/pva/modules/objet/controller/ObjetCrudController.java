package ma.org.ancfcc.pva.modules.objet.controller;

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
import ma.org.ancfcc.pva.modules.objet.Objet;
import ma.org.ancfcc.pva.modules.objet.dto.ObjetDto;
import ma.org.ancfcc.pva.modules.objet.dto.ObjetDtoMapper;
import ma.org.ancfcc.pva.modules.objet.dto.request.ObjetRequestDto;
import ma.org.ancfcc.pva.modules.objet.service.ObjetService;

@RestController
@RequestMapping(value = "/api/v1/objets")
@RequiredArgsConstructor
@Tag(name = "Objet", description = "Objet API")
public class ObjetCrudController extends BaseController<Objet> {

        private static final String ENTITY_NAME = "objet";

        private final ObjetService objetService;

        private final ObjetDtoMapper objetDtoMapper;

        @Operation(summary = "create " + ENTITY_NAME, responses = {
                        @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ObjetDto.class))),
                        @ApiResponse(responseCode = "402", description = "Unprocessable entity", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @PostMapping("")
        @PreAuthorize("hasAuthority('objet:create')")
        public ResponseEntity<RestResponse<ObjetDto>> createObjet(
                        @Validated(OnCreate.class) @RequestBody ObjetRequestDto requestDto) {
                Objet objet = objetService.create(requestDto);
                return buildResponseEntity(objet, ObjetDto.class, HttpStatus.CREATED);

        }

        // *********** UPDATE OPERATIONS ***********

        @Operation(summary = "Update " + ENTITY_NAME, responses = {
                        @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ObjetDto.class))),
                        @ApiResponse(responseCode = "402", description = "Unprocessable entity", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @PutMapping("{id}")
        @PreAuthorize("hasAuthority('objet:update')")
        public ResponseEntity<RestResponse<ObjetDto>> updateObjet(@PathVariable UUID id,
                        @Validated(OnUpdate.class) @RequestBody ObjetRequestDto objetRequestDto) {
                Objet objet = objetService.update(id, objetRequestDto);
                return buildResponseEntity(objet, ObjetDto.class, HttpStatus.OK);
        }

        @Operation(summary = "Delete " + ENTITY_NAME, responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/{id}")
        @PreAuthorize("hasAuthority('objet:delete')")
        public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
                return handleDelete(() -> objetService.delete(id));
        }

        @Operation(summary = "Delete multiple " + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/bulk")
        @PreAuthorize("hasAuthority('objet:delete')")
        public ResponseEntity<Void> deleteMultiple(@RequestBody List<UUID> ids) {
                return handleDelete(() -> objetService.deleteAllById(ids));
        }

        @Operation(summary = "Delete all " + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/all")
        @PreAuthorize("hasAuthority('objet:delete')")
        public ResponseEntity<Void> deleteAll() {
                return handleDelete(objetService::deleteAll);
        }

        @Operation(summary = "Delete all except specified IDs " + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/exclude")
        @PreAuthorize("hasAuthority('objet:delete')")
        public ResponseEntity<Void> deleteAllExcept(@RequestBody List<UUID> ids) {
                return handleDelete(() -> objetService.deleteAllExceptIds(ids));
        }

        @Operation(summary = "Delete by query parameters " + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/query")
        @PreAuthorize("hasAuthority('objet:delete')")
        public ResponseEntity<RestResponse<List<UUID>>> deleteByQueryParams(
                        @RequestParam(value = "filters", defaultValue = "") List<String> filters,
                        @RequestParam(value = "globalFilter", defaultValue = "") String globalFilter) {

                List<UUID> deletedIds = objetService.deleteBySpecification(filters, globalFilter,
                                Objet.class);
                return buildResponseEntity(deletedIds, HttpStatus.OK);

        }

        @Operation(summary = "Delete by query parameters  except ids" + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/query-exclude")
        @PreAuthorize("hasAuthority('objet:delete')")
        public ResponseEntity<RestResponse<List<UUID>>> deleteByQueryParamsExceptIds(
                        @RequestBody List<UUID> ids,
                        @RequestParam(value = "filters", defaultValue = "") List<String> filters,
                        @RequestParam(value = "globalFilter", defaultValue = "") String globalFilter) {

                List<UUID> deletedIds = objetService.deleteBySpecificationExceptIds(filters, globalFilter,
                                Objet.class, ids);

                return buildResponseEntity(deletedIds, HttpStatus.OK);

        }

        @Override
        protected <DTO> DTO mapToDto(Objet entity, Class<DTO> dtoClass) {
                return dtoClass.cast(objetDtoMapper.mapToDto(entity));
        }

}