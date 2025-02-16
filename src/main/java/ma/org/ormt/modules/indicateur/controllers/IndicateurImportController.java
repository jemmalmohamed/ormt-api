package ma.org.ormt.modules.indicateur.controllers;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
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
import ma.org.ormt.modules.indicateur.services.IndicateurImportService;

@RestController
@RequestMapping("/api/v1/indicateurs/import")
@RequiredArgsConstructor
@Log4j2
@Tag(name = "Indicateur Import", description = "Indicateur Import API")
public class IndicateurImportController {

    private final IndicateurImportService indicateurImportService;

    @Operation(summary = "Import indicateurs from Excel file", responses = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ImportResult.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = "ErrorResponse")),
            @ApiResponse(responseCode = "403", description = "Permission denied", content = @Content(mediaType = "ErrorResponse"))
    })
    @PostMapping(value = "/excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('domaine:create')")
    public ResponseEntity<RestResponse<ImportResult>> importFromExcel(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "sheetName", required = false) String sheetName) {

        try {
            if (file.isEmpty()) {
                return ResponseEntity
                        .badRequest()
                        .body(RestResponse.<ImportResult>builder()
                                .success(false)
                                .message("Please select a file to upload")
                                .build());
            }

            String contentType = file.getContentType();
            if (!isExcelContentType(contentType)) {
                return ResponseEntity
                        .badRequest()
                        .body(RestResponse.<ImportResult>builder()
                                .success(false)
                                .message("Please upload an Excel file (xlsx or xls)")
                                .build());
            }

            ImportResult result = indicateurImportService.importFromExcel(file.getInputStream(), sheetName);

            return ResponseEntity.ok(RestResponse.<ImportResult>builder()
                    .success(true)
                    .data(result)
                    .message("Import completed successfully")
                    .build());

        } catch (Exception e) {
            log.error("Error importing Excel file: ", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RestResponse.<ImportResult>builder()
                            .success(false)
                            .message("Error importing file: " + e.getMessage())
                            .build());
        }
    }

    private boolean isExcelContentType(String contentType) {
        return contentType != null && (contentType.equals("application/vnd.ms-excel") ||
                contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
    }

    @Schema(description = "Import operation result")
    public static class ImportResult {
        @Schema(description = "Number of records successfully imported")
        private int successCount;

        @Schema(description = "Number of records failed to import")
        private int failureCount;

        @Schema(description = "List of error messages for failed imports")
        private List<String> errors = new ArrayList<>();

        @Schema(description = "Extracted row data list")
        private List<Map<Integer, String>> rowDataList = new ArrayList<>();

        // Getters and setters
        public int getSuccessCount() {
            return successCount;
        }

        public void setSuccessCount(int successCount) {
            this.successCount = successCount;
        }

        public int getFailureCount() {
            return failureCount;
        }

        public void setFailureCount(int failureCount) {
            this.failureCount = failureCount;
        }

        public List<String> getErrors() {
            return errors;
        }

        public void setErrors(List<String> errors) {
            this.errors = errors;
        }

        public List<Map<Integer, String>> getRowDataList() {
            return rowDataList;
        }

        public void setRowDataList(List<Map<Integer, String>> rowDataList) {
            this.rowDataList = rowDataList;
        }
    }
}
