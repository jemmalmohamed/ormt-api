package ma.org.ormt.modules.configsnapshot.dtos;

import java.util.ArrayList;
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
public class ConfigSnapshotGraphesFileDto {

    @Builder.Default
    private List<GrapheDto> grapheConfigurations = new ArrayList<>();

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GrapheDto {
        private String indicateurNom;
        private String nom;
        private String grapheTypeCode;
        private String grapheTypeNom;
        private String dimensionMappingJson;
        private String chartOptionsJson;
        private Integer chartSpecVersion;
        private String chartSpecJson;
        private String configSystem;
        private Boolean isDefault;
    }
}
