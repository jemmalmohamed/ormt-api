package ma.org.ormt.modules.indicateurs.indicateur.controllers;

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
import ma.org.ormt.core.validators.groups.OnUpdate;
import ma.org.ormt.modules.indicateurs.indicateur.association.dtos.IndicateurDimensionRequestDto;
import ma.org.ormt.modules.indicateurs.indicateur.dtos.IndicateurDto;
import ma.org.ormt.modules.indicateurs.indicateur.dtos.IndicateurDtoMapper;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;
import ma.org.ormt.modules.indicateurs.indicateur.services.IndicateurDimensionService;

@RestController
@RequestMapping(value = "/api/v1/indicateurs")
@RequiredArgsConstructor
@Tag(name = "Indicateur", description = "Indicateur API")
public class IndicateurDimensionController extends BaseController<Indicateur> {

        private static final String ENTITY_NAME = "indicateur";

        private final IndicateurDimensionService indicateurDimensionService;

        private final IndicateurDtoMapper indicateurDtoMapper;

        @Operation(summary = "Update " + ENTITY_NAME, responses = {
                        @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = IndicateurDto.class))),
                        @ApiResponse(responseCode = "402", description = "Unprocessable entity", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @PostMapping("attach-dimension")
        @PreAuthorize("hasAuthority('domaine:edit')")
        public ResponseEntity<RestResponse<IndicateurDto>> associateDimensionToIndicateur(
                        @Validated(OnCreate.class) @RequestBody IndicateurDimensionRequestDto requestDto) {
                Indicateur indicateur = indicateurDimensionService
                                .associateDimensionToIndicateur(requestDto);
                return buildResponseEntity(indicateur, IndicateurDto.class, HttpStatus.OK);
        }

        @Operation(summary = "detach " + ENTITY_NAME, responses = {
                        @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = IndicateurDto.class))),
                        @ApiResponse(responseCode = "402", description = "Unprocessable entity", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @PostMapping("detach-dimension")
        @PreAuthorize("hasAuthority('domaine:edit')")
        public ResponseEntity<RestResponse<IndicateurDto>> dessociateDimensionToIndicateur(
                        @Validated(OnCreate.class) @RequestBody List<Long> ids) {
                indicateurDimensionService.dissociateDimensionFromIndicateur(ids);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        @Override
        protected <DTO> DTO mapToDto(Indicateur entity, Class<DTO> dtoClass) {
                return dtoClass.cast(indicateurDtoMapper.mapToDto(entity));
        }

}