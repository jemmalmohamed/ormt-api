package ma.org.ormt.modules.dashboard.tableaubord.association.domaine.dtos.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReorderTBDomainesRequest {
    @NotNull
    private Long tableauBordId;

    @NotEmpty
    @Valid
    private List<ReorderTBDomaineItem> items;
}
