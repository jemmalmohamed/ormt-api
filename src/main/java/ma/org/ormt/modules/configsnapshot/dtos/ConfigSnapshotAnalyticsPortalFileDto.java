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
public class ConfigSnapshotAnalyticsPortalFileDto {

    @Builder.Default
    private List<DomaineAnalytiqueDto> domainesAnalytiques = new ArrayList<>();

    @Builder.Default
    private List<CategorieAnalytiqueDto> categoriesAnalytiques = new ArrayList<>();

    @Builder.Default
    private List<EspaceLinkDto> espaces = new ArrayList<>();

    @Builder.Default
    private List<TbGroupLinkDto> tbGroups = new ArrayList<>();

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DomaineAnalytiqueDto {
        private String nom;
        private String titre;
        private String description;
        private String apropos;
        private String imageUrl;
        private String slug;
        private String sourceThemeKey;
        private String metadataJson;
        private Boolean actif;
        @Builder.Default
        private List<SectionDto> sections = new ArrayList<>();
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategorieAnalytiqueDto {
        private String domaineSlug;
        private String nom;
        private String libelle;
        private String description;
        private String slug;
        private Integer ordre;
        private Boolean actif;
        private String tbdNom;
        @Builder.Default
        private List<SectionDto> sections = new ArrayList<>();
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SectionDto {
        private String type;
        private String titre;
        private String contentJson;
        private Integer ordre;
        private Boolean actif;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EspaceLinkDto {
        private String espaceNom;
        @Builder.Default
        private List<String> domaines = new ArrayList<>();
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TbGroupLinkDto {
        private String tbGroupNom;
        @Builder.Default
        private List<String> domaines = new ArrayList<>();
    }
}
