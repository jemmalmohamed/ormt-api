package ma.org.ormt.modules.espaces.association.domaine.dtos.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReorderDomainesRequest {
    @NotNull
    private Long espaceId;

    @NotEmpty
    @Valid
    private List<ReorderDomaineItem> items;
}
