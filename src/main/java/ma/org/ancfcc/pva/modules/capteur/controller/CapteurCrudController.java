package ma.org.ancfcc.pva.modules.capteur.controller;

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
import ma.org.ancfcc.pva.modules.capteur.Capteur;
import ma.org.ancfcc.pva.modules.capteur.dto.CapteurDto;
import ma.org.ancfcc.pva.modules.capteur.dto.CapteurDtoMapper;
import ma.org.ancfcc.pva.modules.capteur.dto.request.CapteurRequestDto;
import ma.org.ancfcc.pva.modules.capteur.service.CapteurService;

@RestController
@RequestMapping(value = "/api/v1/capteurs")
@RequiredArgsConstructor
@Tag(name = "Capteur", description = "Capteur API")
public class CapteurCrudController extends BaseController<Capteur> {

        private static final String ENTITY_NAME = "capteur";

        private final CapteurService capteurService;

        private final CapteurDtoMapper capteurDtoMapper;

        @Operation(summary = "create " + ENTITY_NAME, responses = {
                        @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CapteurDto.class))),
                        @ApiResponse(responseCode = "402", description = "Unprocessable entity", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @PostMapping("")
        @PreAuthorize("hasAuthority('capteur:create')")
        public ResponseEntity<RestResponse<CapteurDto>> createCapteur(
                        @Validated(OnCreate.class) @RequestBody CapteurRequestDto requestDto) {
                Capteur capteur = capteurService.create(requestDto);
                return buildResponseEntity(capteur, CapteurDto.class, HttpStatus.CREATED);

        }

        // *********** UPDATE OPERATIONS ***********

        @Operation(summary = "Update " + ENTITY_NAME, responses = {
                        @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CapteurDto.class))),
                        @ApiResponse(responseCode = "402", description = "Unprocessable entity", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @PutMapping("{id}")
        @PreAuthorize("hasAuthority('capteur:update')")
        public ResponseEntity<RestResponse<CapteurDto>> updateCapteur(@PathVariable UUID id,
                        @Validated(OnUpdate.class) @RequestBody CapteurRequestDto capteurRequestDto) {
                Capteur capteur = capteurService.update(id, capteurRequestDto);
                return buildResponseEntity(capteur, CapteurDto.class, HttpStatus.OK);
        }

        @Operation(summary = "Delete " + ENTITY_NAME, responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/{id}")
        @PreAuthorize("hasAuthority('capteur:delete')")
        public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
                return handleDelete(() -> capteurService.delete(id));
        }

        @Operation(summary = "Delete multiple " + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/bulk")
        @PreAuthorize("hasAuthority('capteur:delete')")
        public ResponseEntity<Void> deleteMultiple(@RequestBody List<UUID> ids) {
                return handleDelete(() -> capteurService.deleteAllById(ids));
        }

        @Operation(summary = "Delete all " + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/all")
        @PreAuthorize("hasAuthority('capteur:delete')")
        public ResponseEntity<Void> deleteAll() {
                return handleDelete(capteurService::deleteAll);
        }

        @Operation(summary = "Delete all except specified IDs " + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/exclude")
        @PreAuthorize("hasAuthority('capteur:delete')")
        public ResponseEntity<Void> deleteAllExcept(@RequestBody List<UUID> ids) {
                return handleDelete(() -> capteurService.deleteAllExceptIds(ids));
        }

        @Operation(summary = "Delete by query parameters " + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/query")
        @PreAuthorize("hasAuthority('capteur:delete')")
        public ResponseEntity<RestResponse<List<UUID>>> deleteByQueryParams(
                        @RequestParam(value = "filters", defaultValue = "") List<String> filters,
                        @RequestParam(value = "globalFilter", defaultValue = "") String globalFilter) {

                List<UUID> deletedIds = capteurService.deleteBySpecification(filters, globalFilter,
                                Capteur.class);
                return buildResponseEntity(deletedIds, HttpStatus.OK);

        }

        @Operation(summary = "Delete by query parameters  except ids" + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/query-exclude")
        @PreAuthorize("hasAuthority('capteur:delete')")
        public ResponseEntity<RestResponse<List<UUID>>> deleteByQueryParamsExceptIds(
                        @RequestBody List<UUID> ids,
                        @RequestParam(value = "filters", defaultValue = "") List<String> filters,
                        @RequestParam(value = "globalFilter", defaultValue = "") String globalFilter) {

                List<UUID> deletedIds = capteurService.deleteBySpecificationExceptIds(filters, globalFilter,
                                Capteur.class, ids);

                return buildResponseEntity(deletedIds, HttpStatus.OK);

        }

        @Override
        protected <DTO> DTO mapToDto(Capteur entity, Class<DTO> dtoClass) {
                return dtoClass.cast(capteurDtoMapper.mapToDto(entity));
        }

}