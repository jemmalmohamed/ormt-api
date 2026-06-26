package ma.org.ormt.modules.analytics.domain.dtos.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReorderItemsRequest {

    @NotEmpty
    @Valid
    private List<ReorderByIdItem> items;
}
