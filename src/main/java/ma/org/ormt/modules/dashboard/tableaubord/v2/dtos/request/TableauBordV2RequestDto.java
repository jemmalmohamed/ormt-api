package ma.org.ormt.modules.dashboard.tableaubord.v2.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.Dto;
import ma.org.ormt.modules.dashboard.tableaubord.v2.enums.TableauBordV2Status;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TableauBordV2RequestDto extends Dto {

    @NotBlank(message = "Ce champ est requis.")
    private String nom;

    @NotBlank(message = "Ce champ est requis.")
    private String titre;

    private String sousTitre;

    private String description;

    private String source;

    @NotNull(message = "Ce champ est requis.")
    private Boolean actif;

    private TableauBordV2Status status;

    private Long categorieId;

    private String themeJson;

    private String settingsJson;
}
