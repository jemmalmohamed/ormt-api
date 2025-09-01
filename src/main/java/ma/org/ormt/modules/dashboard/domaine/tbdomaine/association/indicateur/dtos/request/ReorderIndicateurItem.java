package ma.org.ormt.modules.dashboard.domaine.tbdomaine.association.indicateur.dtos.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReorderIndicateurItem {

    @NotNull
    private Long tbDomaineIndicateurId; // ID of the association row to move

    @NotNull
    @Min(0)
    private Integer ordre; // new ordre (0-based)
}
