package ma.org.ormt.modules.espaces.controllers;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
import ma.org.ormt.modules.espaces.dtos.EspaceDto;
import ma.org.ormt.modules.espaces.dtos.EspaceDtoMapper;
import ma.org.ormt.modules.espaces.dtos.request.EspaceRequestDto;
import ma.org.ormt.modules.espaces.models.Espace;
import ma.org.ormt.modules.espaces.services.EspaceService;

@RestController
@RequestMapping(value = "/api/v1/espaces")
@RequiredArgsConstructor
@Tag(name = "Espace", description = "Espace API")
public class EspaceCrudController extends BaseController<Espace> {

        private static final String ENTITY_NAME = "espace";

        private final EspaceService espaceService;
        private final EspaceDtoMapper espaceDtoMapper;

        @Operation(summary = "create " + ENTITY_NAME, responses = {
                        @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EspaceDto.class))),
                        @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @PostMapping("")
        @PreAuthorize("hasAuthority('espace:create')")
        public ResponseEntity<RestResponse<EspaceDto>> createEspace(
                        @Validated(OnCreate.class) @ModelAttribute EspaceRequestDto requestDto) {
                Espace espace = espaceService.create(requestDto);
                return buildResponseEntity(espace, EspaceDto.class, HttpStatus.CREATED);
        }

        @Operation(summary = "Update " + ENTITY_NAME, responses = {
                        @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EspaceDto.class))),
                        @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @PutMapping("{id}")
        @PreAuthorize("hasAuthority('espace:edit')")
        public ResponseEntity<RestResponse<EspaceDto>> updateEspace(@PathVariable Long id,
                        @Validated(OnUpdate.class) @ModelAttribute EspaceRequestDto espaceRequestDto) {
                Espace espace = espaceService.update(id, espaceRequestDto);
                return buildResponseEntity(espace, EspaceDto.class, HttpStatus.OK);
        }

        @Operation(summary = "Delete " + ENTITY_NAME, responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/{id}")
        @PreAuthorize("hasAuthority('espace:delete')")
        public ResponseEntity<Void> deleteById(@PathVariable Long id) {
                return handleDelete(() -> espaceService.delete(id));
        }

        @Operation(summary = "Delete multiple " + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/bulk")
        @PreAuthorize("hasAuthority('espace:delete')")
        public ResponseEntity<Map<String, String>> deleteMultiple(@RequestBody List<Long> ids) {
                try {
                        espaceService.deleteAllById(ids);
                        return ResponseEntity.noContent().build();
                } catch (DataIntegrityViolationException e) {
                        // Handle foreign key constraint violation
                        Map<String, String> response = new HashMap<>();
                        response.put("message", "Suppression impossible, les données sont utilisées ailleurs");
                        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                } catch (Exception e) {
                        // Handle other exceptions
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }
                // return handleDelete(() -> espaceService.deleteAllById(ids));
        }

        @Operation(summary = "Delete all " + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/all")
        @PreAuthorize("hasAuthority('espace:delete')")
        public ResponseEntity<Void> deleteAll() {
                return handleDelete(espaceService::deleteAll);
        }

        @Operation(summary = "Delete all except specified IDs " + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/exclude")
        @PreAuthorize("hasAuthority('espace:delete')")
        public ResponseEntity<Void> deleteAllExcept(@RequestBody List<Long> ids) {
                return handleDelete(() -> espaceService.deleteAllExceptIds(ids));
        }

        @Operation(summary = "Delete by query parameters " + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/query")
        @PreAuthorize("hasAuthority('espace:delete')")
        public ResponseEntity<RestResponse<List<Long>>> deleteByQueryParams(
                        @RequestParam(value = "filters", defaultValue = "") List<String> filters,
                        @RequestParam(value = "globalFilter", defaultValue = "") String globalFilter) {
                List<Long> deletedIds = espaceService.deleteBySpecification(filters, globalFilter,
                                Espace.class);
                return buildResponseEntity(deletedIds, HttpStatus.OK);
        }

        @Operation(summary = "Delete by query parameters except ids" + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/query-exclude")
        @PreAuthorize("hasAuthority('espace:delete')")
        public ResponseEntity<RestResponse<List<Long>>> deleteByQueryParamsExceptIds(
                        @RequestBody List<Long> ids,
                        @RequestParam(value = "filters", defaultValue = "") List<String> filters,
                        @RequestParam(value = "globalFilter", defaultValue = "") String globalFilter) {
                List<Long> deletedIds = espaceService.deleteBySpecificationExceptIds(filters, globalFilter,
                                Espace.class, ids);
                return buildResponseEntity(deletedIds, HttpStatus.OK);
        }

        // @Operation(summary = "Associate espace with indicateur", responses = {
        // @ApiResponse(responseCode = "204", description = "No content"),
        // @ApiResponse(responseCode = "404", description = "Not found", content =
        // @Content(mediaType = "ErrorResponse")),
        // @ApiResponse(responseCode = "403", description = "Permission denied", content
        // = @Content(mediaType = "ErrorResponse"))
        // })
        // @PostMapping("/associate")
        // @PreAuthorize("hasAuthority('espace:edit')")
        // public ResponseEntity<Void> associateWithIndicateur(@RequestBody
        // IndicateurEspaceDto associationDto) {
        // espaceService.associateWithIndicateur(associationDto.getIdEspace(),
        // associationDto.getIdIndicateur());
        // return ResponseEntity.noContent().build();
        // }

        // @Operation(summary = "Dissociate espace from indicateur", responses = {
        // @ApiResponse(responseCode = "204", description = "No content"),
        // @ApiResponse(responseCode = "404", description = "Not found", content =
        // @Content(mediaType = "ErrorResponse")),
        // @ApiResponse(responseCode = "403", description = "Permission denied", content
        // = @Content(mediaType = "ErrorResponse"))
        // })
        // @DeleteMapping("/dissociate")
        // @PreAuthorize("hasAuthority('espace:edit')")
        // public ResponseEntity<Void> dissociateFromIndicateur(@RequestBody
        // IndicateurEspaceDto associationDto) {
        // espaceService.dissociateFromIndicateur(associationDto.getIdEspace(),
        // associationDto.getIdIndicateur());
        // return ResponseEntity.noContent().build();
        // }

        @Override
        protected <DTO> DTO mapToDto(Espace entity, Class<DTO> dtoClass) {
                return dtoClass.cast(espaceDtoMapper.mapToDto(entity));
        }
}