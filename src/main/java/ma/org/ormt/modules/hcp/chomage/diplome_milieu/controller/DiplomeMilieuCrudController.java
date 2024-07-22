package ma.org.ormt.modules.hcp.chomage.diplome_milieu.controller;

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
import ma.org.ormt.modules.hcp.chomage.diplome_milieu.DiplomeMilieu;
import ma.org.ormt.modules.hcp.chomage.diplome_milieu.dto.DiplomeMilieuDto;
import ma.org.ormt.modules.hcp.chomage.diplome_milieu.dto.DiplomeMilieuDtoMapper;
import ma.org.ormt.modules.hcp.chomage.diplome_milieu.dto.request.DiplomeMilieuRequestDto;
import ma.org.ormt.modules.hcp.chomage.diplome_milieu.service.DiplomeMilieuService;

@RestController
@RequestMapping(value = "/api/v1/hcp-chomage-diplome-milieu")
@RequiredArgsConstructor
@Tag(name = "DiplomeMilieu", description = "DiplomeMilieu API")
public class DiplomeMilieuCrudController extends BaseController<DiplomeMilieu> {

        private static final String ENTITY_NAME = "hcp-chomage-diplome-milieu";

        private final DiplomeMilieuService diplomeMilieuService;

        private final DiplomeMilieuDtoMapper diplomeMilieuDtoMapper;

        @Operation(summary = "create " + ENTITY_NAME, responses = {
                        @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DiplomeMilieuDto.class))),
                        @ApiResponse(responseCode = "402", description = "Unprocessable entity", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @PostMapping("")
        @PreAuthorize("hasAuthority('hcp:create')")
        public ResponseEntity<RestResponse<DiplomeMilieuDto>> createDiplomeMilieu(
                        @Validated(OnCreate.class) @RequestBody DiplomeMilieuRequestDto requestDto) {
                DiplomeMilieu diplomeMilieu = diplomeMilieuService.create(requestDto);
                return buildResponseEntity(diplomeMilieu, DiplomeMilieuDto.class, HttpStatus.CREATED);

        }

        // *********** UPDATE OPERATIONS ***********

        @Operation(summary = "Update " + ENTITY_NAME, responses = {
                        @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DiplomeMilieuDto.class))),
                        @ApiResponse(responseCode = "402", description = "Unprocessable entity", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @PutMapping("{id}")
        @PreAuthorize("hasAuthority('hcp:update')")
        public ResponseEntity<RestResponse<DiplomeMilieuDto>> updateDiplomeMilieu(@PathVariable Long id,
                        @Validated(OnUpdate.class) @RequestBody DiplomeMilieuRequestDto diplomeMilieuRequestDto) {
                DiplomeMilieu diplomeMilieu = diplomeMilieuService.update(id, diplomeMilieuRequestDto);
                return buildResponseEntity(diplomeMilieu, DiplomeMilieuDto.class, HttpStatus.OK);
        }

        @Operation(summary = "Delete " + ENTITY_NAME, responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/{id}")
        @PreAuthorize("hasAuthority('hcp:delete')")
        public ResponseEntity<Void> deleteById(@PathVariable Long id) {
                return handleDelete(() -> diplomeMilieuService.delete(id));
        }

        @Operation(summary = "Delete multiple " + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/bulk")
        @PreAuthorize("hasAuthority('hcp:delete')")
        public ResponseEntity<Void> deleteMultiple(@RequestBody List<Long> ids) {
                return handleDelete(() -> diplomeMilieuService.deleteAllById(ids));
        }

        @Operation(summary = "Delete all " + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/all")
        @PreAuthorize("hasAuthority('hcp:delete')")
        public ResponseEntity<Void> deleteAll() {
                return handleDelete(diplomeMilieuService::deleteAll);
        }

        @Operation(summary = "Delete all except specified IDs " + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/exclude")
        @PreAuthorize("hasAuthority('hcp:delete')")
        public ResponseEntity<Void> deleteAllExcept(@RequestBody List<Long> ids) {
                return handleDelete(() -> diplomeMilieuService.deleteAllExceptIds(ids));
        }

        @Operation(summary = "Delete by query parameters " + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/query")
        @PreAuthorize("hasAuthority('hcp:delete')")
        public ResponseEntity<RestResponse<List<Long>>> deleteByQueryParams(
                        @RequestParam(value = "filters", defaultValue = "") List<String> filters,
                        @RequestParam(value = "globalFilter", defaultValue = "") String globalFilter) {

                List<Long> deletedIds = diplomeMilieuService.deleteBySpecification(filters, globalFilter,
                                DiplomeMilieu.class);
                return buildResponseEntity(deletedIds, HttpStatus.OK);

        }

        @Operation(summary = "Delete by query parameters  except ids" + ENTITY_NAME + "s", responses = {
                        @ApiResponse(responseCode = "204", description = "No content"),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @DeleteMapping("/query-exclude")
        @PreAuthorize("hasAuthority('hcp:delete')")
        public ResponseEntity<RestResponse<List<Long>>> deleteByQueryParamsExceptIds(
                        @RequestBody List<Long> ids,
                        @RequestParam(value = "filters", defaultValue = "") List<String> filters,
                        @RequestParam(value = "globalFilter", defaultValue = "") String globalFilter) {

                List<Long> deletedIds = diplomeMilieuService.deleteBySpecificationExceptIds(filters, globalFilter,
                                DiplomeMilieu.class, ids);

                return buildResponseEntity(deletedIds, HttpStatus.OK);

        }

        @Override
        protected <DTO> DTO mapToDto(DiplomeMilieu entity, Class<DTO> dtoClass) {
                return dtoClass.cast(diplomeMilieuDtoMapper.mapToDto(entity));
        }

}