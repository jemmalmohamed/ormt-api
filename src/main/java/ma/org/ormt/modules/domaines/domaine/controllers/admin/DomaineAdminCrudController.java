package ma.org.ormt.modules.domaines.domaine.controllers.admin;

import java.util.List;

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
import lombok.extern.log4j.Log4j2;
import ma.org.ormt.core.commun.base.controller.BaseController;
import ma.org.ormt.core.commun.rest.responses.RestResponse;
import ma.org.ormt.core.exceptions.handlers.DependencyException;
import ma.org.ormt.core.validators.groups.OnCreate;
import ma.org.ormt.core.validators.groups.OnUpdate;
import ma.org.ormt.modules.domaines.domaine.dtos.DomaineDto;
import ma.org.ormt.modules.domaines.domaine.dtos.DomaineDtoMapper;
import ma.org.ormt.modules.domaines.domaine.dtos.request.DomaineRequestDto;
import ma.org.ormt.modules.domaines.domaine.models.Domaine;
import ma.org.ormt.modules.domaines.domaine.services.DomaineService;

@Log4j2
@RestController
@RequestMapping(value = "/api/v1/admin/domaines")
@RequiredArgsConstructor
@Tag(name = "Domaine", description = "Domaine API")
public class DomaineAdminCrudController extends BaseController<Domaine> {

        private static final String ENTITY_NAME = "domaine";

        private final DomaineService domaineService;
        private final DomaineDtoMapper domaineDtoMapper;

        @Operation(summary = "create " + ENTITY_NAME, responses = {
                        @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DomaineDto.class))),
                        @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @PostMapping("")
        @PreAuthorize("hasAuthority('domaine:create')")
        public ResponseEntity<RestResponse<DomaineDto>> createDomaine(
                        @Validated(OnCreate.class) @ModelAttribute DomaineRequestDto requestDto) throws Exception {
                Domaine domaine = domaineService.create(requestDto);
                return buildResponseEntity(domaine, DomaineDto.class, HttpStatus.CREATED);
        }

        @Operation(summary = "Update " + ENTITY_NAME, responses = {
                        @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DomaineDto.class))),
                        @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @PutMapping("{id}")
        @PreAuthorize("hasAuthority('domaine:edit')")
        public ResponseEntity<RestResponse<DomaineDto>> updateDomaine(@PathVariable Long id,
                        @Validated(OnUpdate.class) @ModelAttribute DomaineRequestDto domaineRequestDto)
                        throws Exception {
                Domaine domaine = domaineService.update(id, domaineRequestDto);
                return buildResponseEntity(domaine, DomaineDto.class, HttpStatus.OK);
        }

        @Operation(summary = "Delete " + ENTITY_NAME, responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/{id}")
        @PreAuthorize("hasAuthority('domaine:delete')")
        public ResponseEntity<Void> deleteById(@PathVariable Long id) {
                return handleDelete(() -> domaineService.delete(id));
        }

        @Operation(summary = "Delete multiple " + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/bulk")
        @PreAuthorize("hasAuthority('domaine:delete')")
        public ResponseEntity<RestResponse<List<Long>>> deleteMultiple(@RequestBody List<Long> ids) {
                try {
                        domaineService.deleteAllById(ids);

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

                        RestResponse<List<Long>> errorResponse = RestResponse.<List<Long>>builder()
                                        .success(false)
                                        .message("Une erreur s'est produite lors de la suppression des domaines")
                                        .data(ids)
                                        .build();
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
                }
                // return handleDelete(() -> domaineService.deleteAllById(ids));
        }

        @Operation(summary = "Delete all " + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/all")
        @PreAuthorize("hasAuthority('domaine:delete')")
        public ResponseEntity<Void> deleteAll() {
                return handleDelete(domaineService::deleteAll);
        }

        @Operation(summary = "Delete all except specified IDs " + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/exclude")
        @PreAuthorize("hasAuthority('domaine:delete')")
        public ResponseEntity<Void> deleteAllExcept(@RequestBody List<Long> ids) {
                return handleDelete(() -> domaineService.deleteAllExceptIds(ids));
        }

        @Operation(summary = "Delete by query parameters " + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/query")
        @PreAuthorize("hasAuthority('domaine:delete')")
        public ResponseEntity<RestResponse<List<Long>>> deleteByQueryParams(
                        @RequestParam(value = "filters", defaultValue = "") List<String> filters,
                        @RequestParam(value = "globalFilter", defaultValue = "") String globalFilter) {
                List<Long> deletedIds = domaineService.deleteBySpecification(filters, globalFilter,
                                Domaine.class);
                return buildResponseEntity(deletedIds, HttpStatus.OK);
        }

        @Operation(summary = "Delete by query parameters except ids" + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/query-exclude")
        @PreAuthorize("hasAuthority('domaine:delete')")
        public ResponseEntity<RestResponse<List<Long>>> deleteByQueryParamsExceptIds(
                        @RequestBody List<Long> ids,
                        @RequestParam(value = "filters", defaultValue = "") List<String> filters,
                        @RequestParam(value = "globalFilter", defaultValue = "") String globalFilter) {
                List<Long> deletedIds = domaineService.deleteBySpecificationExceptIds(filters, globalFilter,
                                Domaine.class, ids);
                return buildResponseEntity(deletedIds, HttpStatus.OK);
        }

        @Override
        protected <DTO> DTO mapToDto(Domaine entity, Class<DTO> dtoClass) {
                return dtoClass.cast(domaineDtoMapper.mapToDto(entity));
        }
}