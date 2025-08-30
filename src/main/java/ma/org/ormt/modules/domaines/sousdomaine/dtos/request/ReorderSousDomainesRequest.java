package ma.org.ormt.modules.domaines.sousdomaine.dtos.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReorderSousDomainesRequest {
    @NotNull
    private Long domaineId;

    @NotEmpty
    @Valid
    private List<ReorderSousDomaineItem> items;
}
