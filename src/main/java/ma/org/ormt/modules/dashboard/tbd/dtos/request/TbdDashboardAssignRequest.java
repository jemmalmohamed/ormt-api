package ma.org.ormt.modules.dashboard.tbd.dtos.request;

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

    @NotNull(message = "L'identifiant de la catégorie analytique est requis.")
    private Long categorieAnalytiqueId;
}
