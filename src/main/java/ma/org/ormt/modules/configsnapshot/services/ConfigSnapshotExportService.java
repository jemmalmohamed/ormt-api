package ma.org.ormt.modules.configsnapshot.services;

import ma.org.ormt.modules.configsnapshot.dtos.ConfigSnapshotExportRequestDto;

public interface ConfigSnapshotExportService {

    byte[] exportSnapshot(ConfigSnapshotExportRequestDto requestDto);
}
