package ma.org.ormt.modules.dashboard.tableaubord.association.domaine.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
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
import ma.org.ormt.modules.dashboard.tableaubord.association.domaine.TableauBordDomaine;
import ma.org.ormt.modules.dashboard.tableaubord.association.domaine.dtos.TableauBordDomaineDto;
import ma.org.ormt.modules.dashboard.tableaubord.association.domaine.dtos.TableauBordDomaineDtoMapper;
import ma.org.ormt.modules.dashboard.tableaubord.association.domaine.dtos.request.ReorderTBDomainesRequest;
import ma.org.ormt.modules.dashboard.tableaubord.association.domaine.dtos.request.TableauBordDomaineRequestDto;
import ma.org.ormt.modules.dashboard.tableaubord.association.domaine.service.TableauBordDomaineService;
import ma.org.ormt.modules.dashboard.tableaubord.models.TableauBord;

@RestController
@RequestMapping(value = "/api/v1/tableau-bord-domaine")
@RequiredArgsConstructor
@Tag(name = "TableauBord", description = "TableauBord API")
public class TableauBordDomaineCrudController extends BaseController<TableauBordDomaine> {

        private static final String ENTITY_NAME = "tableauBord";

        private final TableauBordDomaineService tableauBordDomaineService;
        private final TableauBordDomaineDtoMapper tableauBordDomainesDtoMapper;

        // *********** UPDATE OPERATIONS ***********

        @Operation(summary = "attach domaines " + ENTITY_NAME, responses = {
                        @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TableauBord.class))),
                        @ApiResponse(responseCode = "402", description = "Unprocessable entity", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Perdomaines denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @PostMapping("attach-domaines")
        @PreAuthorize("hasAuthority('tableauBord:edit')")
        public ResponseEntity<RestResponse<List<Long>>> attachDomainessToTableauBord(
                        @Validated(OnCreate.class) @RequestBody List<TableauBordDomaineRequestDto> request) {

                List<TableauBordDomaine> tableauBordDomaines = tableauBordDomaineService
                                .attachDomainesToTableauBord(request);

                List<Long> ids = tableauBordDomaines.stream().map(TableauBordDomaine::getId).toList();

                return buildResponseEntity(ids, HttpStatus.OK, true);
        }

        @Operation(summary = "detach domaines " + ENTITY_NAME, responses = {
                        @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TableauBord.class))),
                        @ApiResponse(responseCode = "402", description = "Unprocessable entity", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Perdomaines denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @PostMapping("detach-domaines")
        @PreAuthorize("hasAuthority('tableauBord:edit')")
        public ResponseEntity<RestResponse<List<Long>>> detachDomainesFromTableauBord(
                        @Validated(OnCreate.class) @RequestBody List<Long> deletedIds) {
                tableauBordDomaineService.detachDomainesFromTableauBord(deletedIds);
                return buildResponseEntity(deletedIds, HttpStatus.OK, true);
        }

        @Operation(summary = "reorder domaines for tableauBord", responses = {
                        @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TableauBord.class))),
                        @ApiResponse(responseCode = "402", description = "Unprocessable entity", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Perdomaines denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @PutMapping("reorder-domaines")
        @PreAuthorize("hasAuthority('tableauBord:edit')")
        public ResponseEntity<RestResponse<List<Long>>> reorderDomaines(
                        @Validated(OnCreate.class) @RequestBody ReorderTBDomainesRequest request) {
                tableauBordDomaineService.reorderDomaines(request.getTableauBordId(), request.getItems());
                return buildResponseEntity(java.util.List.of(request.getTableauBordId()), HttpStatus.OK, true);
        }

        @Override
        protected <DTO> DTO mapToDto(TableauBordDomaine entity, Class<DTO> dtoClass) {
                if (dtoClass == TableauBordDomaineDto.class) {
                        return dtoClass.cast(tableauBordDomainesDtoMapper.mapToDto(entity));
                }
                throw new IllegalArgumentException("Unsupported DTO type: " + dtoClass.getName());
        }

}