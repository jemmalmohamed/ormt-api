package ma.org.ormt.modules.dashboard.tableaubord.controllers.admin;

import java.util.List;

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
import ma.org.ormt.modules.dashboard.tableaubord.dtos.TableauBordDto;
import ma.org.ormt.modules.dashboard.tableaubord.dtos.TableauBordDtoMapper;
import ma.org.ormt.modules.dashboard.tableaubord.dtos.request.TableauBordRequestDto;
import ma.org.ormt.modules.dashboard.tableaubord.models.TableauBord;
import ma.org.ormt.modules.dashboard.tableaubord.services.TableauBordService;

@RestController
@RequestMapping(value = "/api/v1/admin/dashboards")
@RequiredArgsConstructor
@Tag(name = "TableauBord", description = "Tableau de bord API")
public class TableauBordAdminCrudController extends BaseController<TableauBord> {

    private static final String ENTITY_NAME = "tableau_bord";

    private final TableauBordService service;
    private final TableauBordDtoMapper dtoMapper;

    @Operation(summary = "create " + ENTITY_NAME, responses = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TableauBordDto.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content(mediaType = "ErrorResponse")),
            @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
    })
    @PostMapping("")
    @PreAuthorize("hasAuthority('dashboard:create')")
    public ResponseEntity<RestResponse<TableauBordDto>> create(
            @Validated(OnCreate.class) @RequestBody TableauBordRequestDto requestDto) throws Exception {
        TableauBord created = service.create(requestDto);
        return buildResponseEntity(created, TableauBordDto.class, HttpStatus.CREATED);
    }

    @Operation(summary = "Update " + ENTITY_NAME, responses = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TableauBordDto.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content(mediaType = "ErrorResponse")),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
            @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
    })
    @PutMapping("{id}")
    @PreAuthorize("hasAuthority('dashboard:edit')")
    public ResponseEntity<RestResponse<TableauBordDto>> update(@PathVariable Long id,
            @Validated(OnUpdate.class) @RequestBody TableauBordRequestDto requestDto) throws Exception {
        TableauBord updated = service.update(id, requestDto);
        return buildResponseEntity(updated, TableauBordDto.class, HttpStatus.OK);
    }

    @Operation(summary = "Delete " + ENTITY_NAME, responses = {
            @ApiResponse(responseCode = "204", description = "No content"),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
            @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('dashboard:delete')")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        return handleDelete(() -> service.delete(id));
    }

    @Operation(summary = "Delete multiple " + ENTITY_NAME + "s", responses = {
            @ApiResponse(responseCode = "204", description = "No content"),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
            @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
    })
    @DeleteMapping("/bulk")
    @PreAuthorize("hasAuthority('dashboard:delete')")
    public ResponseEntity<RestResponse<List<Long>>> deleteMultiple(@RequestBody List<Long> ids) {
        try {
            service.deleteAllById(ids);
            return buildResponseEntity(ids, HttpStatus.OK, true);
        } catch (DataIntegrityViolationException e) {
            RestResponse<List<Long>> errorResponse = RestResponse.<List<Long>>builder()
                    .success(false)
                    .message("Suppression impossible, les données sont utilisées ailleurs")
                    .data(ids)
                    .build();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Delete all " + ENTITY_NAME + "s", responses = {
            @ApiResponse(responseCode = "204", description = "No content"),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
            @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
    })
    @DeleteMapping("/all")
    @PreAuthorize("hasAuthority('dashboard:delete')")
    public ResponseEntity<Void> deleteAll() {
        return handleDelete(service::deleteAll);
    }

    @Override
    protected <DTO> DTO mapToDto(TableauBord entity, Class<DTO> dtoClass) {
        return dtoClass.cast(dtoMapper.mapToDto(entity));
    }
}
