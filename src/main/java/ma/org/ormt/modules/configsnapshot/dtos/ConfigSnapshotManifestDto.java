package ma.org.ormt.modules.configsnapshot.dtos;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

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
public class ConfigSnapshotManifestDto {

    private Integer snapshotVersion;

    private OffsetDateTime exportedAt;

    private String appVersion;

    private List<String> modulesIncluded;

    private Map<String, Object> compatibility;
}
