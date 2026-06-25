package ma.org.ormt.modules.configsnapshot.dtos;

import org.springframework.web.multipart.MultipartFile;

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
public class ConfigSnapshotRestoreRequestDto {

    private MultipartFile file;

    @Builder.Default
    private boolean replace = true;
}
