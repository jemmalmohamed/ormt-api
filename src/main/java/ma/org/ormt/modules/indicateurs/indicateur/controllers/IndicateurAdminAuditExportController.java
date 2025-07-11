package ma.org.ormt.modules.indicateurs.indicateur.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.dtos.IndicateurExportRequestDto;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.multiple.IndicateurExportMultipleService;
import ma.org.ormt.modules.indicateurs.indicateur.services.indicateur.IndicateurService;

@RestController
@RequestMapping("api/v1/admin/indicateurs")
@RequiredArgsConstructor
public class IndicateurAdminAuditExportController {

        private final IndicateurService indicateurService;

        private final IndicateurExportMultipleService indicateurExportMultipleService;

        @Operation(summary = "Exporter les indicateurs avec options personnalisées")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Export OK", content = @Content(mediaType = "application/vnd.ms-excel")),
                        @ApiResponse(responseCode = "400", description = "Paramètres invalides"),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @PostMapping("/export/with-options")
        @PreAuthorize("hasAuthority('indicateur:list')")
        @ResponseBody
        public ResponseEntity<byte[]> exportIndicateurListWithOptions(
                        @RequestBody IndicateurExportRequestDto exportRequest) throws Exception {
                List<Indicateur> indicateurs;

                // Si des IDs spécifiques sont fournis, récupérer uniquement ces indicateurs
                if (exportRequest.getIndicateurIds() != null && !exportRequest.getIndicateurIds().isEmpty()) {
                        indicateurs = indicateurService.findAllById(exportRequest.getIndicateurIds());
                } else {
                        // Sinon, récupérer tous les indicateurs
                        indicateurs = indicateurService.findAll();
                }

                return indicateurExportMultipleService.exportIndicateurListWithOptions(indicateurs, exportRequest);
        }

        // Exporter les indicateurs avec options détaillées par sheet
        // Cette méthode permet d'exporter les indicateurs avec des options détaillées

        @Operation(summary = "Exporter les indicateurs avec options détaillées par sheet")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Export OK", content = @Content(mediaType = "application/vnd.ms-excel")),
                        @ApiResponse(responseCode = "400", description = "Paramètres invalides"),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @PostMapping("/export/details/with-options")
        @PreAuthorize("hasAuthority('indicateur:list')")
        @ResponseBody
        public ResponseEntity<byte[]> exportIndicateursParSheetWithOptions(
                        @RequestBody IndicateurExportRequestDto exportRequest) throws Exception {
                List<Indicateur> indicateurs;
                // Si des IDs spécifiques sont fournis, récupérer uniquement ces indicateurs
                if (exportRequest.getIndicateurIds() != null && !exportRequest.getIndicateurIds().isEmpty()) {
                        indicateurs = indicateurService.findAllById(exportRequest.getIndicateurIds());
                } else {
                        // Sinon, récupérer tous les indicateurs
                        indicateurs = indicateurService.findAll();
                }

                return indicateurExportMultipleService.exportIndicateursParSheetWithOptions(indicateurs, exportRequest);
        }

}
