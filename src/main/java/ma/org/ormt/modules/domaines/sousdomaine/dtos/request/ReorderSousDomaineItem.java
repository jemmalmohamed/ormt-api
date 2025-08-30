package ma.org.ormt.modules.domaines.sousdomaine.dtos.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReorderSousDomaineItem {
    @NotNull
    private Long sousDomaineId; // ID of the sous-domaine to move

    @NotNull
    @Min(0)
    private Integer ordre; // new ordre (0-based)
}
