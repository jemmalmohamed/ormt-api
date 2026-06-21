package ma.org.ormt.modules.dashboard.tbd.categories.dtos.request;

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
public class TbdCategoryRequestDto extends Dto {

    @NotBlank(message = "Ce champ est requis.")
    private String libelle;

    private String description;

    private Integer ordre;

    @NotNull(message = "Ce champ est requis.")
    private Boolean actif;

    @NotNull(message = "Ce champ est requis.")
    private Long tbDomaineId;
}
