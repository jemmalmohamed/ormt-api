package ma.org.ormt.modules.analytics.category.dtos.request;

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
public class CategorieAnalytiqueSectionRequestDto extends Dto {

    @NotBlank(message = "Ce champ est requis.")
    private String type;

    private String titre;

    private String contentJson;

    private Integer ordre;

    @NotNull(message = "Ce champ est requis.")
    private Boolean actif;
}
