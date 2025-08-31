package ma.org.ormt.modules.domaines.sousdomaine.controllers.admin;

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
import ma.org.ormt.modules.domaines.sousdomaine.dtos.SousDomaineDto;
import ma.org.ormt.modules.domaines.sousdomaine.dtos.SousDomaineDtoMapper;
import ma.org.ormt.modules.domaines.sousdomaine.dtos.request.SousDomaineRequestDto;
import ma.org.ormt.modules.domaines.sousdomaine.dtos.request.ReorderSousDomainesRequest;
import ma.org.ormt.modules.domaines.sousdomaine.models.SousDomaine;
import ma.org.ormt.modules.domaines.sousdomaine.services.SousDomaineService;
import ma.org.ormt.modules.indicateurs.indicateur.dtos.IndicateurDto;

@RestController
@RequestMapping(value = "/api/v1/admin/domaines")
@RequiredArgsConstructor
@Tag(name = "SousDomaine", description = "SousDomaine API")
public class SousDomaineCrudController extends BaseController<SousDomaine> {

        private static final String ENTITY_NAME = "sousdomaine";

        private final SousDomaineService sousDomaineService;

        private final SousDomaineDtoMapper sousDomaineDtoMapper;

        @Operation(summary = "create " + ENTITY_NAME, responses = {
                        @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SousDomaineDto.class))),
                        @ApiResponse(responseCode = "402", description = "Unprocessable entity", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @PostMapping("{domaineId}/sous-domaines")
        @PreAuthorize("hasAuthority('domaine:create')")
        public ResponseEntity<RestResponse<SousDomaineDto>> createSousDomaine(
                        @PathVariable Long domaineId,
                        @Validated(OnCreate.class) @RequestBody SousDomaineRequestDto requestDto) {
                SousDomaine sousDomaine = sousDomaineService.create(domaineId, requestDto);
                return buildResponseEntity(sousDomaine, SousDomaineDto.class, HttpStatus.CREATED);

        }

        // *********** UPDATE OPERATIONS ***********

        @Operation(summary = "Update " + ENTITY_NAME, responses = {
                        @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SousDomaineDto.class))),
                        @ApiResponse(responseCode = "402", description = "Unprocessable entity", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @PutMapping("{domaineId}/sous-domaines/{id}")
        @PreAuthorize("hasAuthority('domaine:edit')")
        public ResponseEntity<RestResponse<SousDomaineDto>> updateSousDomaine(
                        @PathVariable Long domaineId,
                        @PathVariable Long id,
                        @Validated(OnUpdate.class) @RequestBody SousDomaineRequestDto sousDomaineRequestDto) {
                SousDomaine sousDomaine = sousDomaineService.update(id, sousDomaineRequestDto);
                return buildResponseEntity(sousDomaine, SousDomaineDto.class, HttpStatus.OK);
        }

        @Operation(summary = "attach indicateur " + ENTITY_NAME, responses = {
                        @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = IndicateurDto.class))),
                        @ApiResponse(responseCode = "402", description = "Unprocessable entity", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @PostMapping("sous-domaines/{id}/attach-indicateurs")
        @PreAuthorize("hasAuthority('domaine:edit')")
        public ResponseEntity<RestResponse<SousDomaineDto>> associateDimensionToIndicateur(
                        @PathVariable Long id,
                        @Validated(OnCreate.class) @RequestBody List<Long> indicateurIds) {
                SousDomaine sousDomaine = sousDomaineService
                                .associateIndicateurToSousDomaine(id, indicateurIds);
                return buildResponseEntity(sousDomaine, SousDomaineDto.class, HttpStatus.OK);
        }

        @Operation(summary = "detach indicateur " + ENTITY_NAME, responses = {
                        @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = IndicateurDto.class))),
                        @ApiResponse(responseCode = "402", description = "Unprocessable entity", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @PostMapping("sous-domaines/{id}/detach-indicateurs")
        @PreAuthorize("hasAuthority('domaine:edit')")
        public ResponseEntity<RestResponse<SousDomaineDto>> dessociateDimensionToIndicateur(
                        @PathVariable Long id,
                        @Validated(OnCreate.class) @RequestBody List<Long> ids) {
                SousDomaine sousDomaine = sousDomaineService.dissociateIndicateurFromSousDomaine(id, ids);
                return buildResponseEntity(sousDomaine, SousDomaineDto.class, HttpStatus.OK);
        }

        @Operation(summary = "reorder sous-domaines for domaine", responses = {
                        @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SousDomaine.class))),
                        @ApiResponse(responseCode = "402", description = "Unprocessable entity", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @PutMapping("{domaineId}/sous-domaines/reorder")
        @PreAuthorize("hasAuthority('domaine:edit')")
        public ResponseEntity<RestResponse<List<Long>>> reorderSousDomaines(
                        @PathVariable Long domaineId,
                        @Validated(OnCreate.class) @RequestBody ReorderSousDomainesRequest request) {
                // If request carries domaineId, prefer path variable and sanity check
                if (request.getDomaineId() != null && !request.getDomaineId().equals(domaineId)) {
                        return buildResponseEntity(java.util.List.of(), HttpStatus.BAD_REQUEST, true);
                }
                sousDomaineService.reorderSousDomaines(domaineId, request.getItems());
                return buildResponseEntity(java.util.List.of(domaineId), HttpStatus.OK, true);
        }

        // *********** DELETE OPERATIONS ***********
        // **************************************** */

        @Operation(summary = "Delete " + ENTITY_NAME, responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("{domaineId}/sous-domaines/{id}")
        @PreAuthorize("hasAuthority('domaine:delete')")
        public ResponseEntity<Void> deleteById(@PathVariable Long domaineId, @PathVariable Long id) {
                return handleDelete(() -> sousDomaineService.delete(id));
        }

        @Operation(summary = "Delete multiple " + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("{domaineId}/sous-domaines/bulk")
        @PreAuthorize("hasAuthority('domaine:delete')")
        public ResponseEntity<Map<String, String>> deleteMultiple(@PathVariable Long domaineId,
                        @RequestBody List<Long> ids) {
                try {
                        sousDomaineService.deleteAllById(ids);
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
        @DeleteMapping("{domaineId}/sous-domaines/all")
        @PreAuthorize("hasAuthority('domaine:delete')")
        public ResponseEntity<Void> deleteAll(@PathVariable Long domaineId) {
                return handleDelete(sousDomaineService::deleteAll);
        }

        @Operation(summary = "Delete all except specified IDs " + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("{domaineId}/sous-domaines/exclude")
        @PreAuthorize("hasAuthority('domaine:delete')")
        public ResponseEntity<Void> deleteAllExcept(@PathVariable Long domaineId, @RequestBody List<Long> ids) {
                return handleDelete(() -> sousDomaineService.deleteAllExceptIds(ids));
        }

        @Operation(summary = "Delete by query parameters " + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("{domaineId}/sous-domaines/query")
        @PreAuthorize("hasAuthority('domaine:delete')")
        public ResponseEntity<RestResponse<List<Long>>> deleteByQueryParams(
                        @PathVariable Long domaineId,
                        @RequestParam(value = "filters", defaultValue = "") List<String> filters,
                        @RequestParam(value = "globalFilter", defaultValue = "") String globalFilter) {

                List<Long> deletedIds = sousDomaineService.deleteBySpecification(filters, globalFilter,
                                SousDomaine.class);
                return buildResponseEntity(deletedIds, HttpStatus.OK, true);

        }

        @Operation(summary = "Delete by query parameters  except ids" + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("{domaineId}/sous-domaines/query-exclude")
        @PreAuthorize("hasAuthority('domaine:delete')")
        public ResponseEntity<RestResponse<List<Long>>> deleteByQueryParamsExceptIds(
                        @PathVariable Long domaineId,
                        @RequestBody List<Long> ids,
                        @RequestParam(value = "filters", defaultValue = "") List<String> filters,
                        @RequestParam(value = "globalFilter", defaultValue = "") String globalFilter) {

                List<Long> deletedIds = sousDomaineService.deleteBySpecificationExceptIds(filters, globalFilter,
                                SousDomaine.class, ids);

                return buildResponseEntity(deletedIds, HttpStatus.OK, true);

        }

        @Override
        protected <DTO> DTO mapToDto(SousDomaine entity, Class<DTO> dtoClass) {
                return dtoClass.cast(sousDomaineDtoMapper.mapToDto(entity));
        }

}