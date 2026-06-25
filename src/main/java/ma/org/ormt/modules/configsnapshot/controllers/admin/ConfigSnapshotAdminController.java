package ma.org.ormt.modules.configsnapshot.controllers.admin;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import ma.org.ormt.core.commun.rest.responses.RestResponse;
import ma.org.ormt.modules.configsnapshot.dtos.ConfigSnapshotExportRequestDto;
import ma.org.ormt.modules.configsnapshot.dtos.ConfigSnapshotRestoreRequestDto;
import ma.org.ormt.modules.configsnapshot.dtos.ConfigSnapshotRestoreResultDto;
import ma.org.ormt.modules.configsnapshot.services.ConfigSnapshotExportService;
import ma.org.ormt.modules.configsnapshot.services.ConfigSnapshotRestoreService;

@RestController
@RequestMapping("api/v1/admin/config-snapshots")
@RequiredArgsConstructor
public class ConfigSnapshotAdminController {

    private static final String ADMIN_ACCESS = "(hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_MASTER'))";

    private final ConfigSnapshotExportService exportService;
    private final ConfigSnapshotRestoreService restoreService;

    @PostMapping("/export")
    @PreAuthorize(ADMIN_ACCESS)
    @Operation(summary = "Export the canonical config snapshot as a ZIP archive.")
    public ResponseEntity<byte[]> exportSnapshot(@RequestBody(required = false) ConfigSnapshotExportRequestDto requestDto) {
        byte[] archive = exportService.exportSnapshot(requestDto);
        String timestamp = OffsetDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"config-snapshot-v1-" + timestamp + ".zip\"")
                .contentType(MediaType.parseMediaType("application/zip"))
                .body(archive);
    }

    @PostMapping(value = "/restore", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize(ADMIN_ACCESS)
    @Operation(summary = "Restore the canonical config snapshot from a ZIP archive.")
    public ResponseEntity<RestResponse<ConfigSnapshotRestoreResultDto>> restoreSnapshot(
            @Validated @ModelAttribute ConfigSnapshotRestoreRequestDto requestDto) {
        ConfigSnapshotRestoreResultDto result = restoreService.restoreSnapshot(requestDto);
        return ResponseEntity.ok(RestResponse.<ConfigSnapshotRestoreResultDto>builder()
                .status(HttpStatus.OK)
                .success(true)
                .message("Config snapshot restored successfully.")
                .data(result)
                .build());
    }
}
