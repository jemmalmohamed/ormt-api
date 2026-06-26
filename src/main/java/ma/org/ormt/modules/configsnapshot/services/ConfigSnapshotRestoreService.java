package ma.org.ormt.modules.configsnapshot.services;

import ma.org.ormt.modules.configsnapshot.dtos.ConfigSnapshotRestoreRequestDto;
import ma.org.ormt.modules.configsnapshot.dtos.ConfigSnapshotRestoreResultDto;

public interface ConfigSnapshotRestoreService {

    ConfigSnapshotRestoreResultDto restoreSnapshot(ConfigSnapshotRestoreRequestDto requestDto);
}
