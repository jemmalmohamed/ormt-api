package ma.org.ormt.modules.indicateurs.indicateur.association.dimension.controllers;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
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
import ma.org.ormt.core.exceptions.handlers.DependencyException;
import ma.org.ormt.core.validators.groups.OnCreate;
import ma.org.ormt.core.validators.groups.OnUpdate;
import ma.org.ormt.modules.espaces.dtos.EspaceDto;
import ma.org.ormt.modules.indicateurs.indicateur.association.dimension.dtos.IndicateurDimensionDto;
import ma.org.ormt.modules.indicateurs.indicateur.association.dimension.dtos.IndicateurDimensionDtoMapper;
import ma.org.ormt.modules.indicateurs.indicateur.association.dimension.dtos.request.IndicateurDimensionRequestDto;
import ma.org.ormt.modules.indicateurs.indicateur.association.dimension.models.IndicateurDimension;
import ma.org.ormt.modules.indicateurs.indicateur.association.dimension.services.IndicateurDimensionService;
import ma.org.ormt.modules.indicateurs.indicateur.dtos.IndicateurDto;

@RestController
@RequestMapping(value = "/api/v1/admin/indicateur/dimensions")
@RequiredArgsConstructor
@Tag(name = "Indicateur", description = "Indicateur API")
public class IndicateurDimensionController extends BaseController<IndicateurDimension> {

        private static final String ENTITY_NAME = "indicateur";

        private final IndicateurDimensionService indicateurDimensionService;

        private final IndicateurDimensionDtoMapper indicateurDimensionDtoMapper;

        @Operation(summary = "Update " + ENTITY_NAME, responses = {
                        @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EspaceDto.class))),
                        @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @PutMapping("{id}")
        @PreAuthorize("hasAuthority('indicateur:edit')")
        public ResponseEntity<RestResponse<IndicateurDimensionDto>> update(@PathVariable Long id,
                        @Validated(OnUpdate.class) @RequestBody IndicateurDimensionRequestDto requestDto)
                        throws Exception {
                IndicateurDimension indicateurDimension = indicateurDimensionService.update(id, requestDto);
                return buildResponseEntity(indicateurDimension, IndicateurDimensionDto.class, HttpStatus.OK);
        }

        @Operation(summary = "attach " + ENTITY_NAME, responses = {
                        @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = IndicateurDto.class))),
                        @ApiResponse(responseCode = "402", description = "Unprocessable entity", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @PostMapping("attach-dimension")
        @PreAuthorize("hasAuthority('indicateur:edit')")
        public ResponseEntity<RestResponse<IndicateurDimensionDto>> associateDimensionToIndicateur(
                        @Validated(OnCreate.class) @RequestBody IndicateurDimensionRequestDto requestDto) {
                IndicateurDimension indicateur = indicateurDimensionService
                                .associateDimensionToIndicateur(requestDto);
                return buildResponseEntity(indicateur, IndicateurDimensionDto.class, HttpStatus.OK);
        }

        @Operation(summary = "detach " + ENTITY_NAME, responses = {
                        @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = IndicateurDto.class))),
                        @ApiResponse(responseCode = "402", description = "Unprocessable entity", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @PostMapping("detach-dimension")
        @PreAuthorize("hasAuthority('indicateur:edit')")
        public ResponseEntity<RestResponse<List<Long>>> dessociateDimensionToIndicateur(
                        @Validated(OnCreate.class) @RequestBody List<Long> ids) {

                try {
                        indicateurDimensionService.dissociateDimensionFromIndicateur(ids);
                        return buildResponseEntity(ids, HttpStatus.OK);

                } catch (DependencyException e) {

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
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }
        }

        @Override
        protected <DTO> DTO mapToDto(IndicateurDimension entity, Class<DTO> dtoClass) {
                return dtoClass.cast(indicateurDimensionDtoMapper.mapToDto(entity));
        }

}