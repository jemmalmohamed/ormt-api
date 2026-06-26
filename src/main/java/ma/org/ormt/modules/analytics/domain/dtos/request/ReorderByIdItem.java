package ma.org.ormt.modules.analytics.domain.dtos.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReorderByIdItem {

    @NotNull
    private Long id;

    @NotNull
    @Min(0)
    private Integer ordre;
}
