package ma.org.ormt.modules.indicateurs.indicateur.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ma.org.ormt.core.commun.rest.responses.RestResponse;

import ma.org.ormt.modules.indicateurs.indicateur.dtos.imports.ImportOrganizedRequest;
import ma.org.ormt.modules.indicateurs.indicateur.helpers.ImportXlsResult;
import ma.org.ormt.modules.indicateurs.indicateur.services.imports.IndicateurImportService;

@RestController
@RequestMapping("/api/v1/indicateurs")
@RequiredArgsConstructor
@Log4j2
@Tag(name = "Indicateur Import", description = "Indicateur Import API")
public class IndicateurImportController {

        private final IndicateurImportService indicateurImportService;

        @Operation(summary = "Import indicateurs from Excel file", responses = {
                        @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ImportXlsResult.class))),
                        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @PostMapping(value = "{indicateurId}/import-excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        @PreAuthorize("hasAuthority('domaine:create')")
        public ResponseEntity<RestResponse<ImportXlsResult>> importFromExcel(
                        @PathVariable Long indicateurId,
                        @RequestParam("file") MultipartFile file,
                        @RequestParam(value = "sheet", required = false) String sheetName) {

                try {
                        if (file.isEmpty()) {
                                return ResponseEntity
                                                .badRequest()
                                                .body(RestResponse.<ImportXlsResult>builder()
                                                                .success(false)
                                                                .message("Please select a file to upload")
                                                                .build());
                        }

                        String contentType = file.getContentType();
                        if (!isExcelContentType(contentType)) {
                                return ResponseEntity
                                                .badRequest()
                                                .body(RestResponse.<ImportXlsResult>builder()
                                                                .success(false)
                                                                .message("Please upload an Excel file (xlsx or xls)")
                                                                .build());
                        }

                        ImportXlsResult result = indicateurImportService.importIndicateurFromExcel(
                                        file.getInputStream(),
                                        sheetName);

                        return ResponseEntity.ok(RestResponse.<ImportXlsResult>builder()
                                        .success(true)
                                        .data(result)
                                        .message("Import completed successfully")
                                        .build());

                } catch (Exception e) {
                        log.error("Error importing Excel file: ", e);
                        return ResponseEntity
                                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(RestResponse.<ImportXlsResult>builder()
                                                        .success(false)
                                                        .message("Error importing file: " + e.getMessage())
                                                        .build());
                }
        }

        @Operation(summary = "Import organized indicateurs from User", responses = {
                        @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ImportXlsResult.class))),
                        @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = "ErrorResponse")),
                        @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
        })
        @PostMapping(value = "/organize-excel")
        @PreAuthorize("hasAuthority('domaine:create')")
        public ResponseEntity<RestResponse<ImportOrganizedRequest>> importFromOrginizedExcel(

                        @RequestBody ImportOrganizedRequest requestDtos) {

                return ResponseEntity.ok(RestResponse.<ImportOrganizedRequest>builder()
                                .success(true)
                                .data(requestDtos)
                                .message("Organized import completed successfully")
                                .build());
        }

        // @Operation(summary = "Import organized indicateurs from User", responses = {
        // @ApiResponse(responseCode = "200", description = "Success", content =
        // @Content(mediaType = "application/json", schema = @Schema(implementation =
        // ImportXlsResult.class))),
        // @ApiResponse(responseCode = "400", description = "Bad request", content =
        // @Content(mediaType = "ErrorResponse")),
        // @ApiResponse(responseCode = "403", description = "Permission denied", content
        // = @Content(mediaType = "ErrorResponse"))
        // })
        // @PostMapping(value = "{indicateurId}/donnee")
        // @PreAuthorize("hasAuthority('domaine:create')")
        // public ResponseEntity<RestResponse<List<DonneeIndicateurDto>>>
        // importDonneeFromOrginizedExcel(
        // @PathVariable Long indicateurId,
        // @RequestBody List<DonneeIndicateurRequestDto> requestDtos) {

        // List<DonneeIndicateurDto> donneeIndicateurDtos =
        // donneeIndicateurService.createByList(indicateurId,
        // requestDtos);

        // return ResponseEntity.ok(RestResponse.<List<DonneeIndicateurDto>>builder()
        // .success(true)
        // .data(donneeIndicateurDtos)
        // .message("Organized import completed successfully")
        // .build());
        // }

        private boolean isExcelContentType(String contentType) {
                return contentType != null && (contentType.equals("application/vnd.ms-excel") ||
                                contentType.equals(
                                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        }

}
