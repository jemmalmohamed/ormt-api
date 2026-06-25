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
public class ConfigSnapshotIndicatorsFileDto {

    @Builder.Default
    private List<IndicatorDto> indicateurs = new ArrayList<>();

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IndicatorDto {
        private String nom;
        private String titre;
        private String description;
        private String abreviation;
        private String categorie;
        private Boolean actif;
        private String typeTb;
        private String unite;
        private String regleCalcul;
        private SourceRefDto source;
        @Builder.Default
        private List<SousDomaineRefDto> sousDomaines = new ArrayList<>();
        @Builder.Default
        private List<DimensionBindingDto> dimensions = new ArrayList<>();
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SourceRefDto {
        private String nom;
        private String abreviation;
        private String description;
        private String url;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SousDomaineRefDto {
        private String domaineNom;
        private String domaineDescription;
        private Boolean domaineActif;
        private String sousDomaineNom;
        private String sousDomaineDescription;
        private Boolean sousDomaineActif;
        private Integer sousDomaineOrdre;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DimensionBindingDto {
        private String nom;
        private String libelle;
        private String type;
        private String description;
        private Boolean principale;
        private Boolean temporelle;
    }
}
