package ma.org.ormt.modules.indicateurs.donnee.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import ma.org.ormt.modules.indicateurs.donnee.dtos.DonneeIndicateurDto;
import ma.org.ormt.modules.indicateurs.donnee.dtos.DonneeIndicateurDtoMapper;
import ma.org.ormt.modules.indicateurs.donnee.dtos.request.DonneeIndicateurRequestDto;
import ma.org.ormt.modules.indicateurs.donnee.models.DonneeIndicateur;
import ma.org.ormt.modules.indicateurs.donnee.services.DonneeIndicateurService;

@RestController
@RequestMapping(value = "/api/v1/admin/indicateurs")
@RequiredArgsConstructor
@Tag(name = "donnne", description = "donneeIndicateur API")
public class DonneeIndicateurCrudController extends BaseController<DonneeIndicateur> {

        private static final String ENTITY_NAME = "donneeIndicateur";

        private final DonneeIndicateurService donneeIndicateurService;

        private final DonneeIndicateurDtoMapper donneeIndicateurDtoMapper;

        @Operation(summary = "create " + ENTITY_NAME, responses = {
                        @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DonneeIndicateurDto.class))),
                        @ApiResponse(responseCode = "402", description = "Unprocessable entity", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @PostMapping("{indicateurId}/donnees")
        @PreAuthorize("hasAuthority('domaine:create')")
        public ResponseEntity<RestResponse<DonneeIndicateurDto>> createDonneeIndicateur(
                        @PathVariable Long indicateurId,
                        @Validated(OnCreate.class) @RequestBody DonneeIndicateurRequestDto requestDto) {
                DonneeIndicateur donneeIndicateur = donneeIndicateurService.create(indicateurId, requestDto);
                return buildResponseEntity(donneeIndicateur, DonneeIndicateurDto.class, HttpStatus.CREATED);

        }

        @Operation(summary = "Create multiple " + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "201", description = "Created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DonneeIndicateurDto.class))),
                        @ApiResponse(responseCode = "402", description = "Unprocessable entity", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @PostMapping("{indicateurId}/donnees/bulk")
        @PreAuthorize("hasAuthority('domaine:create')")
        public ResponseEntity<RestResponse<List<DonneeIndicateurDto>>> createDonneeIndicateurList(
                        @PathVariable Long indicateurId,
                        @Validated(OnCreate.class) @RequestBody List<DonneeIndicateurRequestDto> requestDtos) {
                List<DonneeIndicateur> donneeIndicateurs = donneeIndicateurService.createBulk(indicateurId,
                                requestDtos);

                return buildResponseEntity(donneeIndicateurs, DonneeIndicateurDto.class,
                                HttpStatus.CREATED);
        }

        // *********** UPDATE OPERATIONS ***********

        @Operation(summary = "Update " + ENTITY_NAME, responses = {
                        @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DonneeIndicateurDto.class))),
                        @ApiResponse(responseCode = "402", description = "Unprocessable entity", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @PutMapping("{indicateurId}/donnees/{id}")
        @PreAuthorize("hasAuthority('domaine:edit')")
        public ResponseEntity<RestResponse<DonneeIndicateurDto>> updateDonneeIndicateur(
                        @PathVariable Long indicateurId,
                        @PathVariable Long id,
                        @Validated(OnUpdate.class) @RequestBody DonneeIndicateurRequestDto donneeIndicateurRequestDto) {
                DonneeIndicateur donneeIndicateur = donneeIndicateurService.update(id, donneeIndicateurRequestDto);
                return buildResponseEntity(donneeIndicateur, DonneeIndicateurDto.class, HttpStatus.OK);
        }

        // *********** DELETE OPERATIONS ***********
        // **************************************** */

        @Operation(summary = "Delete " + ENTITY_NAME, responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("{indicateurId}/donnees/{id}")
        @PreAuthorize("hasAuthority('domaine:delete')")
        public ResponseEntity<Void> deleteById(@PathVariable Long indicateurId, @PathVariable Long id) {
                return handleDelete(() -> donneeIndicateurService.delete(id));
        }

        @Operation(summary = "Delete multiple " + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("{indicateurId}/donnees/bulk")
        @PreAuthorize("hasAuthority('domaine:delete')")
        public ResponseEntity<Map<String, String>> deleteMultiple(@PathVariable Long indicateurId,
                        @RequestBody List<Long> ids) {
                try {
                        donneeIndicateurService.deleteAllById(ids);
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
        }

        @Operation(summary = "Delete all " + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("{indicateurId}/donnees/all")
        @PreAuthorize("hasAuthority('domaine:delete')")
        public ResponseEntity<Void> deleteAll(@PathVariable Long indicateurId) {
                return handleDelete(donneeIndicateurService::deleteAll);
        }

        @Operation(summary = "Delete all except specified IDs " + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("{indicateurId}/donnees/exclude")
        @PreAuthorize("hasAuthority('domaine:delete')")
        public ResponseEntity<Void> deleteAllExcept(@PathVariable Long indicateurId, @RequestBody List<Long> ids) {
                return handleDelete(() -> donneeIndicateurService.deleteAllExceptIds(ids));
        }

        @Operation(summary = "Delete by query parameters " + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("{indicateurId}/donnees/query")
        @PreAuthorize("hasAuthority('domaine:delete')")
        public ResponseEntity<RestResponse<List<Long>>> deleteByQueryParams(
                        @PathVariable Long indicateurId,
                        @RequestParam(value = "filters", defaultValue = "") List<String> filters,
                        @RequestParam(value = "globalFilter", defaultValue = "") String globalFilter) {

                List<Long> deletedIds = donneeIndicateurService.deleteBySpecification(filters, globalFilter,
                                DonneeIndicateur.class);
                return buildResponseEntity(deletedIds, HttpStatus.OK);

        }

        @Operation(summary = "Delete by query parameters  except ids" + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("{indicateurId}/donnees/query-exclude")
        @PreAuthorize("hasAuthority('domaine:delete')")
        public ResponseEntity<RestResponse<List<Long>>> deleteByQueryParamsExceptIds(
                        @PathVariable Long indicateurId,
                        @RequestBody List<Long> ids,
                        @RequestParam(value = "filters", defaultValue = "") List<String> filters,
                        @RequestParam(value = "globalFilter", defaultValue = "") String globalFilter) {

                List<Long> deletedIds = donneeIndicateurService.deleteBySpecificationExceptIds(filters, globalFilter,
                                DonneeIndicateur.class, ids);

                return buildResponseEntity(deletedIds, HttpStatus.OK);

        }

        @Override
        protected <DTO> DTO mapToDto(DonneeIndicateur entity, Class<DTO> dtoClass) {
                return dtoClass.cast(donneeIndicateurDtoMapper.mapToDto(entity));
        }

}