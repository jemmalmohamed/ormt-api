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
    private String role;
    private String statut;

    @Data
    @NoArgsConstructor
    public static class SousDomaineCreateRequestDto {
        private String nom;
        private String description;
        private String role;
        private String statut;
        private List<IndicateurCreateRequestDto> indicateurs;
    }

    @Data
    @NoArgsConstructor
    public static class IndicateurCreateRequestDto {
        private String nom;
        private String categorie;
        private String role;
        private String statut;
        private String abreviation;
        private String description;
        private String typeTb;
        private String source;
        private String regleCalcul;
        private String unite;
        private String periode;
        private List<DimensionCreateRequestDto> dimensions;

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
    }

}