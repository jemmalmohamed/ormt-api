package ma.org.ormt.modules.espaces.association.domaine.controller;

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
import ma.org.ormt.modules.espaces.association.domaine.EspaceDomaine;
import ma.org.ormt.modules.espaces.association.domaine.dtos.EspaceDomaineDto;
import ma.org.ormt.modules.espaces.association.domaine.dtos.EspaceDomaineDtoMapper;
import ma.org.ormt.modules.espaces.association.domaine.dtos.request.EspaceDomaineRequestDto;
import ma.org.ormt.modules.espaces.association.domaine.dtos.request.ReorderDomainesRequest;
import ma.org.ormt.modules.espaces.association.domaine.service.EspaceDomaineService;
import ma.org.ormt.modules.espaces.models.Espace;

@RestController
@RequestMapping(value = "/api/v1/espace-domaine")
@RequiredArgsConstructor
@Tag(name = "Espace", description = "Espace API")
public class EspaceDomaineCrudController extends BaseController<EspaceDomaine> {

        private static final String ENTITY_NAME = "espace";

        private final EspaceDomaineService espaceDomaineService;
        private final EspaceDomaineDtoMapper espaceDomainesDtoMapper;

        // *********** UPDATE OPERATIONS ***********

        @Operation(summary = "attach domaines " + ENTITY_NAME, responses = {
                        @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Espace.class))),
                        @ApiResponse(responseCode = "402", description = "Unprocessable entity", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Perdomaines denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @PostMapping("attach-domaines")
        @PreAuthorize("hasAuthority('espace:edit')")
        public ResponseEntity<RestResponse<List<Long>>> attachDomainessToEspace(
                        @Validated(OnCreate.class) @RequestBody List<EspaceDomaineRequestDto> request) {

                List<EspaceDomaine> espaceDomaines = espaceDomaineService
                                .attachDomainesToEspace(request);

                List<Long> ids = espaceDomaines.stream().map(EspaceDomaine::getId).toList();

                return buildResponseEntity(ids, HttpStatus.OK, true);
        }

        @Operation(summary = "detach domaines " + ENTITY_NAME, responses = {
                        @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Espace.class))),
                        @ApiResponse(responseCode = "402", description = "Unprocessable entity", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Perdomaines denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @PostMapping("detach-domaines")
        @PreAuthorize("hasAuthority('espace:edit')")
        public ResponseEntity<RestResponse<List<Long>>> detachDomainesFromEspace(
                        @Validated(OnCreate.class) @RequestBody List<Long> deletedIds) {
                espaceDomaineService.detachDomainesFromEspace(deletedIds);
                return buildResponseEntity(deletedIds, HttpStatus.OK, true);
        }

        @Operation(summary = "reorder domaines for espace", responses = {
                        @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Espace.class))),
                        @ApiResponse(responseCode = "402", description = "Unprocessable entity", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Perdomaines denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @PutMapping("reorder-domaines")
        @PreAuthorize("hasAuthority('espace:edit')")
        public ResponseEntity<RestResponse<List<Long>>> reorderDomaines(
                        @Validated(OnCreate.class) @RequestBody ReorderDomainesRequest request) {
                espaceDomaineService.reorderDomaines(request.getEspaceId(), request.getItems());
                return buildResponseEntity(java.util.List.of(request.getEspaceId()), HttpStatus.OK, true);
        }

        @Override
        protected <DTO> DTO mapToDto(EspaceDomaine entity, Class<DTO> dtoClass) {
                if (dtoClass == EspaceDomaineDto.class) {
                        return dtoClass.cast(espaceDomainesDtoMapper.mapToDto(entity));
                }
                throw new IllegalArgumentException("Unsupported DTO type: " + dtoClass.getName());
        }

}