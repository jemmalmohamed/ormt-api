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
public class ConfigSnapshotTbdDashboardsFileDto {

    @Builder.Default
    private List<DashboardDto> tbdDashboards = new ArrayList<>();

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DashboardDto {
        private String nom;
        private String titre;
        private String sousTitre;
        private String description;
        private String sourceText;
        private Boolean actif;
        private String status;
        @Builder.Default
        private List<SourceRefDto> sources = new ArrayList<>();
        @Builder.Default
        private List<SectionDto> sections = new ArrayList<>();
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SourceRefDto {
        private String sourceNom;
        private String sourceAbreviation;
        private Integer ordre;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SectionDto {
        private String label;
        private Integer ordre;
        private Integer sizePercent;
        private Boolean actif;
        @Builder.Default
        private List<RowDto> rows = new ArrayList<>();
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RowDto {
        private Integer ordre;
        private Integer sizePercent;
        private Integer heightPx;
        @Builder.Default
        private List<WidgetDto> widgets = new ArrayList<>();
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WidgetDto {
        private String type;
        private String indicateurNom;
        private String chiffreCleLibelle;
        private String contentJson;
        private String titre;
        private Integer ordre;
        private Integer sizePercent;
        private Boolean actif;
    }
}
