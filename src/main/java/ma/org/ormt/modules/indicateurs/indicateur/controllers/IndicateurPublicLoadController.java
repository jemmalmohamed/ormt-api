package ma.org.ormt.modules.indicateurs.indicateur.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import ma.org.ormt.core.commun.rest.responses.RestResponse;
import ma.org.ormt.modules.indicateurs.indicateur.dtos.detail.IndicateurDetailDto;
import ma.org.ormt.modules.indicateurs.indicateur.dtos.detail.IndicateurDetailDtoMapper;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;
import ma.org.ormt.modules.indicateurs.indicateur.services.indicateur.IndicateurService;

@RestController
@RequestMapping("api/v1/public/indicateurs")
@RequiredArgsConstructor
public class IndicateurPublicLoadController {

    private static final String ENTITY_NAME = "indicateur";

    private final IndicateurService indicateurService;
    private final IndicateurDetailDtoMapper indicateurDetailMapper;

    @Operation(summary = "Get public " + ENTITY_NAME + " by id",
            description = "Get indicateur details with optional table data. " +
                    "Use tableFormat parameter: 'pivot' for pivot table, 'flat' for flat table, 'both' for both formats")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = IndicateurDetailDto.class)) }),
            @ApiResponse(responseCode = "404", description = ENTITY_NAME + " not found",
                    content = @Content(mediaType = "ErrorResponse"))
    })
    @GetMapping("/{id}")
    public ResponseEntity<RestResponse<IndicateurDetailDto>> getIndicateur(
            @PathVariable("id") Long id,
            @Parameter() @RequestParam(value = "tableFormat", required = false) String tableFormat) {

        IndicateurDetailDto indicateurDetail;
        if (tableFormat != null && !tableFormat.isEmpty()) {
            indicateurDetail = indicateurService.getIndicateurWithTableData(id, tableFormat);
        } else {
            Indicateur indicateur = indicateurService.findById(id)
                    .orElseThrow(EntityNotFoundException::new);
            indicateurDetail = indicateurDetailMapper.mapToDto(indicateur);
        }

        return ResponseEntity.ok(RestResponse.<IndicateurDetailDto>builder()
                .data(indicateurDetail)
                .build());
    }
}
