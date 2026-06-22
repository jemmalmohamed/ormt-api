package ma.org.ormt.modules.analytics.category.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.Dto;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CategorieAnalytiqueRequestDto extends Dto {

    @NotNull(message = "Ce champ est requis.")
    private Long domaineAnalytiqueId;

    private String nom;

    @jakarta.validation.constraints.NotBlank(message = "Ce champ est requis.")
    private String libelle;

    private String description;

    private String slug;

    private Integer ordre;

    private Long tbdDashboardId;

    @NotNull(message = "Ce champ est requis.")
    private Boolean actif;
}
