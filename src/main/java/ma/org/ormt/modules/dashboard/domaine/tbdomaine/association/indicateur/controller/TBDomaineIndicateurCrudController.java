package ma.org.ormt.modules.dashboard.domaine.tbdomaine.association.indicateur.controller;

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
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.association.indicateur.TBDomaineIndicateur;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.association.indicateur.dtos.TBDomaineIndicateurDto;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.association.indicateur.dtos.TBDomaineIndicateurDtoMapper;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.association.indicateur.dtos.request.ReorderIndicateursRequest;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.association.indicateur.dtos.request.TBDomaineIndicateurRequestDto;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.association.indicateur.service.TBDomaineIndicateurService;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.models.TBDomaine;

@RestController
@RequestMapping(value = "/api/v1/admin/tb-domaine-indicateur")
@RequiredArgsConstructor
@Tag(name = "TBDomaine", description = "TBDomaine API")
public class TBDomaineIndicateurCrudController extends BaseController<TBDomaineIndicateur> {

        private static final String ENTITY_NAME = "TBDomaine";

        private final TBDomaineIndicateurService tbDomaineIndicateurService;
        private final TBDomaineIndicateurDtoMapper tbDomaineIndicateursDtoMapper;

        // *********** UPDATE OPERATIONS ***********

        @Operation(summary = "attach indicateurs " + ENTITY_NAME, responses = {
                        @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TBDomaine.class))),
                        @ApiResponse(responseCode = "402", description = "Unprocessable entity", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Perindicateurs denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @PostMapping("attach-indicateurs")
        @PreAuthorize("hasAuthority('dashboard:edit')")
        public ResponseEntity<RestResponse<List<Long>>> attachIndicateursToTBDomaine(
                        @Validated(OnCreate.class) @RequestBody List<TBDomaineIndicateurRequestDto> request) {

                List<TBDomaineIndicateur> tbDomaineIndicateurs = tbDomaineIndicateurService
                                .attachIndicateursToTBDomaine(request);

                List<Long> ids = tbDomaineIndicateurs.stream().map(TBDomaineIndicateur::getId).toList();

                return buildResponseEntity(ids, HttpStatus.OK, true);
        }

        @Operation(summary = "detach indicateurs " + ENTITY_NAME, responses = {
                        @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TBDomaine.class))),
                        @ApiResponse(responseCode = "402", description = "Unprocessable entity", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Perindicateurs denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @PostMapping("detach-indicateurs")
        @PreAuthorize("hasAuthority('dashboard:edit')")
        public ResponseEntity<RestResponse<List<Long>>> detachIndicateursFromTBDomaine(
                        @Validated(OnCreate.class) @RequestBody List<Long> deletedIds) {
                tbDomaineIndicateurService.detachIndicateursFromTBDomaine(deletedIds);
                return buildResponseEntity(deletedIds, HttpStatus.OK, true);
        }

        @Operation(summary = "reorder indicateurs for TBDomaine", responses = {
                        @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TBDomaine.class))),
                        @ApiResponse(responseCode = "402", description = "Unprocessable entity", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Perindicateurs denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @PutMapping("reorder-indicateurs")
        @PreAuthorize("hasAuthority('dashboard:edit')")
        public ResponseEntity<RestResponse<List<Long>>> reorderIndicateurs(
                        @Validated(OnCreate.class) @RequestBody ReorderIndicateursRequest request) {
                tbDomaineIndicateurService.reorderIndicateurs(request.getTbDomaineId(), request.getItems());
                return buildResponseEntity(java.util.List.of(request.getTbDomaineId()), HttpStatus.OK, true);
        }

        @Override
        protected <DTO> DTO mapToDto(TBDomaineIndicateur entity, Class<DTO> dtoClass) {
                if (dtoClass == TBDomaineIndicateurDto.class) {
                        return dtoClass.cast(tbDomaineIndicateursDtoMapper.mapToDto(entity));
                }
                throw new IllegalArgumentException("Unsupported DTO type: " + dtoClass.getName());
        }

}