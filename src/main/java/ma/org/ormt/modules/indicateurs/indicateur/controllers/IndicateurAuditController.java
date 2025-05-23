package ma.org.ormt.modules.indicateurs.indicateur.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.helpers.IndicateurExportAuditService;
import ma.org.ormt.modules.indicateurs.indicateur.services.indicateur.IndicateurService;

@RestController
@RequestMapping("api/v1/indicateurs/audit")
@RequiredArgsConstructor
public class IndicateurAuditController {

        private final IndicateurService indicateurService;

        private final IndicateurExportAuditService indicateurExportService;

        @Operation(summary = "Exporter les indicateurs (nom et description) en Excel")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Export OK", content = @Content(mediaType = "application/vnd.ms-excel")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @GetMapping("/export")
        @PreAuthorize("hasAuthority('indicateur:list')")
        @ResponseBody
        public ResponseEntity<byte[]> exportIndicateurs() throws Exception {
                List<Indicateur> indicateurs = indicateurService.findAll();
                return indicateurExportService.exportIndicateursAudit(indicateurs);
        }

        @Operation(summary = "Exporter les indicateurs (nom et description) en Excel")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Export OK", content = @Content(mediaType = "application/vnd.ms-excel")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @GetMapping("/export/details")
        @PreAuthorize("hasAuthority('indicateur:list')")
        @ResponseBody
        public ResponseEntity<byte[]> exportIndicateurParSheet() throws Exception {
                List<Indicateur> indicateurs = indicateurService.findAll();

                return indicateurExportService.exportIndicateursParSheet(indicateurs);
        }

}
