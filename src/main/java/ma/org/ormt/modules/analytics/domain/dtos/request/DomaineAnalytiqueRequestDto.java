package ma.org.ormt.modules.analytics.domain.dtos.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.Dto;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DomaineAnalytiqueRequestDto extends Dto {

    private String nom;

    @jakarta.validation.constraints.NotBlank(message = "Ce champ est requis.")
    private String titre;

    private String description;

    @jakarta.validation.constraints.NotBlank(message = "Ce champ est requis.")
    @Size(min = 200, max = 5000, message = "Le texte doit contenir au moins 200 caractères.")
    private String apropos;

    private String imageUrl;

    private MultipartFile imageFile;

    private String slug;

    private String sourceThemeKey;

    private String metadataJson;

    @NotNull(message = "Ce champ est requis.")
    private Boolean actif;
}
