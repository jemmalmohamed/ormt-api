package ma.org.ormt.modules.configsnapshot.dtos;

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
public class ConfigSnapshotExportRequestDto {

    @Builder.Default
    private boolean includeLegacyInitData = true;
}
