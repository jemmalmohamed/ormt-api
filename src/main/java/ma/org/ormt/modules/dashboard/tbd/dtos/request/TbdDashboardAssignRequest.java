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
public class TbdDashboardAssignRequest {

    @NotBlank(message = "Le type de cible est requis.")
    private String cibleType;

    @NotNull(message = "L'identifiant de la cible est requis.")
    private Long cibleId;
}
