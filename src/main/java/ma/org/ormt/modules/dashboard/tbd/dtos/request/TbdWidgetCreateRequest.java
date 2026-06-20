package ma.org.ormt.modules.dashboard.tbd.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TbdWidgetCreateRequest {

    @NotNull(message = "L'identifiant de la rangée est requis.")
    private Long rowId;

    @NotBlank(message = "Le type est requis.")
    private String type;

    private Long indicateurId;

    private Long kpiId;

    private String titre;

    private Integer ordre;

    private Integer sizePercent;

    private String contentJson;
}
