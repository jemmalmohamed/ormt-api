package ma.org.ormt.modules.indicateurs.dimension.dtos;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DomaineCreateRequestDto {
    private String nom;
    private String description;
    private String apropos;

    private Boolean actif;

    @Data
    @NoArgsConstructor
    public static class SousDomaineCreateRequestDto {
        private String nom;
        private String description;

        private Boolean actif;
        private List<IndicateurCreateRequestDto> indicateurs;
    }

    @Data
    @NoArgsConstructor
    public static class IndicateurCreateRequestDto {
        private String nom;
        private String categorie;

        private Boolean actif;
        private String abreviation;
        private String description;
        private String typeTb;
        private String source;
        private String regleCalcul;
        private String unite;
        private String periode;
        private List<DimensionCreateRequestDto> dimensions;
        private List<GrapheConfigurationCreateRequestDto> grapheConfigurations;

        @Data
        @NoArgsConstructor
        public static class DimensionCreateRequestDto {
            private String nom;
            private String libelle;
            private String type;
            private AssociationRequestDto association;
        }

        @Data
        @NoArgsConstructor
        public static class AssociationRequestDto {
            private Boolean principale;
            private Boolean temporelle;
        }

        @Data
        @NoArgsConstructor
        public static class IndicateurDonneeRequestDto {
            private String indicateur;
            private List<Object> data;
        }

        @Data
        @NoArgsConstructor
        public static class GrapheConfigurationCreateRequestDto {
            private String nom; // optional
            private String grapheTypeCode; // required
            private Boolean isDefault = false;
            private String dimensionMappingJson; // optional; default {"default":"standard"}
            private String chartOptionsJson; // optional
        }
    }

}
