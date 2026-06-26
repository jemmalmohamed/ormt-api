package ma.org.ormt.modules.configsnapshot.services.impl;

import lombok.Builder;
import lombok.Getter;
import ma.org.ormt.modules.configsnapshot.dtos.ConfigSnapshotAnalyticsPortalFileDto;
import ma.org.ormt.modules.configsnapshot.dtos.ConfigSnapshotChiffresFileDto;
import ma.org.ormt.modules.configsnapshot.dtos.ConfigSnapshotDonneesFileDto;
import ma.org.ormt.modules.configsnapshot.dtos.ConfigSnapshotGraphesFileDto;
import ma.org.ormt.modules.configsnapshot.dtos.ConfigSnapshotIndicatorsFileDto;
import ma.org.ormt.modules.configsnapshot.dtos.ConfigSnapshotManifestDto;
import ma.org.ormt.modules.configsnapshot.dtos.ConfigSnapshotTbdDashboardsFileDto;

@Getter
@Builder
public class ConfigSnapshotArchive {

    private final ConfigSnapshotManifestDto manifest;
    private final ConfigSnapshotIndicatorsFileDto indicators;
    private final ConfigSnapshotDonneesFileDto donnees;
    private final ConfigSnapshotGraphesFileDto graphes;
    private final ConfigSnapshotChiffresFileDto chiffres;
    private final ConfigSnapshotTbdDashboardsFileDto dashboards;
    private final ConfigSnapshotAnalyticsPortalFileDto analytics;
}
