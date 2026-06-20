package ma.org.ormt.modules.dashboard.tbd.dtos.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TbdDashboardUpdateRequest {

    @NotBlank(message = "Le nom est requis.")
    private String nom;

    private String titre;

    private String sousTitre;

    private String description;

    private String periodeLabel;
}
