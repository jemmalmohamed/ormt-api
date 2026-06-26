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
public class ConfigSnapshotDonneesFileDto {

    @Builder.Default
    private List<RowDto> donneesIndicateurs = new ArrayList<>();

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RowDto {
        private String indicateurNom;
        private String valeur;
        @Builder.Default
        private List<DimensionValueDto> dimensions = new ArrayList<>();
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DimensionValueDto {
        private String dimensionNom;
        private String valeur;
    }
}
