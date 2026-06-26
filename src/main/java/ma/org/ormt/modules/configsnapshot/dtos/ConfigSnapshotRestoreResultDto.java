package ma.org.ormt.modules.configsnapshot.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigSnapshotRestoreResultDto {

    private Integer snapshotVersion;

    private boolean replacedExistingData;

    private List<String> modulesRestored;

    private int sourcesCreated;

    private int dimensionsCreated;

    private int indicateursCreated;

    private int donneesCreated;

    private int graphesCreated;

    private int chiffresClesCreated;

    private int dashboardsCreated;

    private int analyticsDomainesCreated;

    private int analyticsCategoriesCreated;

    private String restoredBy;
}
