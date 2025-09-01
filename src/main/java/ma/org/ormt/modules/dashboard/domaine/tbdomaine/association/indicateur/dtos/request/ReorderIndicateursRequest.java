package ma.org.ormt.modules.dashboard.domaine.tbdomaine.association.indicateur.dtos.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReorderIndicateursRequest {
    @NotNull
    private Long tbDomaineId;

    @NotEmpty
    @Valid
    private List<ReorderIndicateurItem> items;
}
