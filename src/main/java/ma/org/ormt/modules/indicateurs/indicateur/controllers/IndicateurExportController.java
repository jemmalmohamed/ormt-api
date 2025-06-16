package ma.org.ormt.modules.indicateurs.indicateur.controllers;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import ma.org.ormt.modules.indicateurs.indicateur.dtos.export.IndicateurExportRequest;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.single.IndicateurExportService;
import ma.org.ormt.modules.indicateurs.indicateur.services.indicateur.IndicateurService;

@RestController
@RequestMapping("api/v1/indicateurs")
@RequiredArgsConstructor
public class IndicateurExportController {

        private static final String ENTITY_NAME = "indicateur";

        private final IndicateurService indicateurService;
        private final IndicateurExportService indicarteurExportService;

        /**
         * Exporte les données d'un indicateur en format Excel, avec différentes options
         * de mise en page
         * 
         * @param id     L'ID de l'indicateur à exporter
         * @param format Le format d'exportation (EXCEL ou CSV)
         * @param layout Le type de mise en page (CLASSIC, PIVOT, BOTH)
         * @return Le fichier exporté
         * @throws IOException En cas d'erreur d'exportation
         */
        @Operation(summary = "Exporter les données d'un indicateur")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Ok", content = {
                                        @Content(mediaType = "application/vnd.ms-excel") }),
                        @ApiResponse(responseCode = "404", description = ENTITY_NAME
                                        + " not found", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @PostMapping("/{id}/export")
        @PreAuthorize("hasAuthority('indicateur:read')")
        public ResponseEntity<byte[]> exportIndicateur(
                        @PathVariable("id") Long id,

                        @RequestBody IndicateurExportRequest requestDto)
                        throws IOException {

                Indicateur indicateur = indicateurService.findById(id)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Indicateur non trouvé avec l'ID: " + id));

                return indicarteurExportService.exportIndicateurDonnees(indicateur, requestDto);

        }

}