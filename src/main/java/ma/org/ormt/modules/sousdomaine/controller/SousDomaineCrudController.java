package ma.org.ormt.modules.sousdomaine.controller;

import java.util.List;

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
import ma.org.ormt.modules.sousdomaine.SousDomaine;
import ma.org.ormt.modules.sousdomaine.dto.SousDomaineDto;
import ma.org.ormt.modules.sousdomaine.dto.SousDomaineDtoMapper;
import ma.org.ormt.modules.sousdomaine.dto.request.SousDomaineRequestDto;
import ma.org.ormt.modules.sousdomaine.service.SousDomaineService;

@RestController
@RequestMapping(value = "/api/v1/sousdomaines")
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
        @PostMapping("")
        @PreAuthorize("hasAuthority('sousdomaine:create')")
        public ResponseEntity<RestResponse<SousDomaineDto>> createSousDomaine(
                        @Validated(OnCreate.class) @RequestBody SousDomaineRequestDto requestDto) {
                SousDomaine sousDomaine = sousDomaineService.create(requestDto);
                return buildResponseEntity(sousDomaine, SousDomaineDto.class, HttpStatus.CREATED);

        }

        // *********** UPDATE OPERATIONS ***********

        @Operation(summary = "Update " + ENTITY_NAME, responses = {
                        @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SousDomaineDto.class))),
                        @ApiResponse(responseCode = "402", description = "Unprocessable entity", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @PutMapping("{id}")
        @PreAuthorize("hasAuthority('sousdomaine:update')")
        public ResponseEntity<RestResponse<SousDomaineDto>> updateSousDomaine(@PathVariable Long id,
                        @Validated(OnUpdate.class) @RequestBody SousDomaineRequestDto sousDomaineRequestDto) {
                SousDomaine sousDomaine = sousDomaineService.update(id, sousDomaineRequestDto);
                return buildResponseEntity(sousDomaine, SousDomaineDto.class, HttpStatus.OK);
        }

        @Operation(summary = "Delete " + ENTITY_NAME, responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/{id}")
        @PreAuthorize("hasAuthority('sousdomaine:delete')")
        public ResponseEntity<Void> deleteById(@PathVariable Long id) {
                return handleDelete(() -> sousDomaineService.delete(id));
        }

        @Operation(summary = "Delete multiple " + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/bulk")
        @PreAuthorize("hasAuthority('sousdomaine:delete')")
        public ResponseEntity<Void> deleteMultiple(@RequestBody List<Long> ids) {
                return handleDelete(() -> sousDomaineService.deleteAllById(ids));
        }

        @Operation(summary = "Delete all " + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/all")
        @PreAuthorize("hasAuthority('sousdomaine:delete')")
        public ResponseEntity<Void> deleteAll() {
                return handleDelete(sousDomaineService::deleteAll);
        }

        @Operation(summary = "Delete all except specified IDs " + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/exclude")
        @PreAuthorize("hasAuthority('sousdomaine:delete')")
        public ResponseEntity<Void> deleteAllExcept(@RequestBody List<Long> ids) {
                return handleDelete(() -> sousDomaineService.deleteAllExceptIds(ids));
        }

        @Operation(summary = "Delete by query parameters " + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/query")
        @PreAuthorize("hasAuthority('sousdomaine:delete')")
        public ResponseEntity<RestResponse<List<Long>>> deleteByQueryParams(
                        @RequestParam(value = "filters", defaultValue = "") List<String> filters,
                        @RequestParam(value = "globalFilter", defaultValue = "") String globalFilter) {

                List<Long> deletedIds = sousDomaineService.deleteBySpecification(filters, globalFilter,
                                SousDomaine.class);
                return buildResponseEntity(deletedIds, HttpStatus.OK);

        }

        @Operation(summary = "Delete by query parameters  except ids" + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/query-exclude")
        @PreAuthorize("hasAuthority('sousdomaine:delete')")
        public ResponseEntity<RestResponse<List<Long>>> deleteByQueryParamsExceptIds(
                        @RequestBody List<Long> ids,
                        @RequestParam(value = "filters", defaultValue = "") List<String> filters,
                        @RequestParam(value = "globalFilter", defaultValue = "") String globalFilter) {

                List<Long> deletedIds = sousDomaineService.deleteBySpecificationExceptIds(filters, globalFilter,
                                SousDomaine.class, ids);

                return buildResponseEntity(deletedIds, HttpStatus.OK);

        }

        @Override
        protected <DTO> DTO mapToDto(SousDomaine entity, Class<DTO> dtoClass) {
                return dtoClass.cast(sousDomaineDtoMapper.mapToDto(entity));
        }

}