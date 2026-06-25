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
public class ConfigSnapshotChiffresFileDto {

    @Builder.Default
    private List<ChiffreDto> chiffresCles = new ArrayList<>();

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChiffreDto {
        private String libelle;
        private String valeur;
        private String unite;
        private String description;
        private Boolean afficherDate;
        private Boolean afficherDescription;
        private Boolean actif;
        private String accessType;
        private String modeSource;
        private String formatType;
        private String prefixLabel;
        private String suffixLabel;
        private String evolutionMode;
        private String metadataJson;
        private String styleJson;
        private String indicateurNom;
        private DonneeReferenceDto donneeReference;
        @Builder.Default
        private List<String> domaineNoms = new ArrayList<>();
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DonneeReferenceDto {
        private String indicateurNom;
        @Builder.Default
        private List<ConfigSnapshotDonneesFileDto.DimensionValueDto> dimensions = new ArrayList<>();
        private String valeur;
    }
}
