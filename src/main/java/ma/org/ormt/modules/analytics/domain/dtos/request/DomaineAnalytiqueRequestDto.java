package ma.org.ormt.modules.analytics.domain.dtos.request;

import jakarta.validation.constraints.NotBlank;
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
public class DomaineAnalytiqueRequestDto extends Dto {

    @NotBlank(message = "Ce champ est requis.")
    private String nom;

    @NotBlank(message = "Ce champ est requis.")
    private String titre;

    private String description;

    private String apropos;

    private String imageUrl;

    private String slug;

    private String sourceThemeKey;

    private String metadataJson;

    @NotNull(message = "Ce champ est requis.")
    private Boolean actif;
}
