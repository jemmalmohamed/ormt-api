package ma.org.ormt.modules.dashboard.tbd.dtos.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TbdWidgetRowHeightUpdateRequest {

    @NotNull
    @Min(48)
    private Integer heightPx;
}
