package ma.org.ormt.modules.indicateurs.indicateur.controllers;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.dtos.IndicateurExportRequestDto;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.single.IndicateurSingleExportService;
import ma.org.ormt.modules.indicateurs.indicateur.services.indicateur.IndicateurService;

@Tag(name = "Single Indicateur Export", description = "API pour l'export d'un indicateur unique avec options avancées")
@RestController
@RequestMapping("api/v1/admin/indicateurs/{indicateurId}/export")
@RequiredArgsConstructor
public class SingleIndicateurExportController {

        private final IndicateurService indicateurService;
        private final IndicateurSingleExportService singleExportService;

        @Operation(summary = "Exporter un indicateur complet (workbook avec toutes les sections)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Export réussi", content = @Content(mediaType = "application/vnd.ms-excel")),
                        @ApiResponse(responseCode = "400", description = "Paramètres invalides"),
                        @ApiResponse(responseCode = "404", description = "Indicateur non trouvé"),
                        @ApiResponse(responseCode = "403", description = "Permission refusée")
        })
        @PostMapping("/workbook")
        @PreAuthorize("hasAuthority('indicateur:read')")
        @ResponseBody
        public ResponseEntity<byte[]> exportFullWorkbook(
                        @Parameter(description = "ID de l'indicateur à exporter", required = true) @PathVariable Long indicateurId,
                        @Parameter(description = "Configuration de l'export", required = true) @RequestBody IndicateurExportRequestDto exportRequest)
                        throws IOException {

                Indicateur indicateur = indicateurService.findById(indicateurId)
                                .orElseThrow(() -> new RuntimeException(
                                                "Indicateur non trouvé avec l'ID: " + indicateurId));

                // Configure for full workbook export with all sections

                return singleExportService.exportIndicateurWithOptions(indicateur, exportRequest);
        }

        @Operation(summary = "Exporter un indicateur avec options sélectives")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Export réussi", content = @Content(mediaType = "application/vnd.ms-excel")),
                        @ApiResponse(responseCode = "400", description = "Paramètres invalides"),
                        @ApiResponse(responseCode = "404", description = "Indicateur non trouvé"),
                        @ApiResponse(responseCode = "403", description = "Permission refusée")
        })
        @PostMapping("/selective")
        @PreAuthorize("hasAuthority('indicateur:read')")
        @ResponseBody
        public ResponseEntity<byte[]> exportSelective(
                        @Parameter(description = "ID de l'indicateur à exporter", required = true) @PathVariable Long indicateurId,
                        @Parameter(description = "Configuration sélective de l'export", required = true) @RequestBody IndicateurExportRequestDto exportRequest)
                        throws IOException {

                Indicateur indicateur = indicateurService.findById(indicateurId)
                                .orElseThrow(() -> new RuntimeException(
                                                "Indicateur non trouvé avec l'ID: " + indicateurId));

                // Use provided configuration, applying defaults where needed
                IndicateurExportRequestDto configuredRequest = IndicateurExportRequestDto.builder()
                                .format(exportRequest.getFormat() != null ? exportRequest.getFormat()
                                                : IndicateurExportRequestDto.ExportFormat.EXCEL)
                                .fileName(exportRequest.getFileName() != null ? exportRequest.getFileName()
                                                : "indicateur-" + indicateur.getId() + "-selectif")
                                .sectionsToExport(
                                                exportRequest.getSectionsToExport() != null
                                                                && !exportRequest.getSectionsToExport().isEmpty()
                                                                                ? exportRequest.getSectionsToExport()
                                                                                : java.util.List.of(
                                                                                                IndicateurExportRequestDto.ExportSection.META
                                                                                                                .getKey(),
                                                                                                IndicateurExportRequestDto.ExportSection.PIVOT_DATA
                                                                                                                .getKey()))
                                .dataTableType(exportRequest.getDataTableType() != null
                                                ? exportRequest.getDataTableType()
                                                : IndicateurExportRequestDto.DataTableType.PIVOT)
                                .build();

                return singleExportService.exportIndicateurWithOptions(indicateur, configuredRequest);
        }

        @Operation(summary = "Exporter uniquement les métadonnées d'un indicateur")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Export réussi", content = @Content(mediaType = "application/vnd.ms-excel")),
                        @ApiResponse(responseCode = "400", description = "Paramètres invalides"),
                        @ApiResponse(responseCode = "404", description = "Indicateur non trouvé"),
                        @ApiResponse(responseCode = "403", description = "Permission refusée")
        })
        @PostMapping("/metadata-only")
        @PreAuthorize("hasAuthority('indicateur:export')")
        @ResponseBody
        public ResponseEntity<byte[]> exportMetadataOnly(
                        @Parameter(description = "ID de l'indicateur à exporter", required = true) @PathVariable Long indicateurId,
                        @Parameter(description = "Configuration de l'export des métadonnées", required = true) @RequestBody IndicateurExportRequestDto exportRequest)
                        throws IOException {

                Indicateur indicateur = indicateurService.findById(indicateurId)
                                .orElseThrow(() -> new RuntimeException(
                                                "Indicateur non trouvé avec l'ID: " + indicateurId));

                // Configure for metadata-only export
                IndicateurExportRequestDto metadataRequest = IndicateurExportRequestDto.builder()
                                .format(exportRequest.getFormat() != null ? exportRequest.getFormat()
                                                : IndicateurExportRequestDto.ExportFormat.EXCEL)
                                .fileName(exportRequest.getFileName() != null ? exportRequest.getFileName()
                                                : "indicateur-" + indicateur.getId() + "-metadata")
                                .sectionsToExport(java.util.List.of(
                                                IndicateurExportRequestDto.ExportSection.META.getKey(),
                                                IndicateurExportRequestDto.ExportSection.DOMAINES.getKey(),
                                                IndicateurExportRequestDto.ExportSection.DIMENSIONS.getKey()))
                                .dataTableType(IndicateurExportRequestDto.DataTableType.NONE)
                                .build();

                return singleExportService.exportIndicateurWithOptions(indicateur, metadataRequest);
        }
}
