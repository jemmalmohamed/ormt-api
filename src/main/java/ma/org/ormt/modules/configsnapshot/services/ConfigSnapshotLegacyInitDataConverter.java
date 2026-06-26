package ma.org.ormt.modules.configsnapshot.services;

import java.io.IOException;
import java.util.List;

import ma.org.ormt.modules.configsnapshot.services.impl.ConfigSnapshotArchive;

public interface ConfigSnapshotLegacyInitDataConverter {

    List<ConfigSnapshotZipEntry> buildLegacyEntries(ConfigSnapshotArchive archive) throws IOException;
}
