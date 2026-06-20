package ma.org.ormt.modules.dashboard.tbd.dtos.request;

import jakarta.validation.constraints.Max;
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
public class TbdSectionSizeItem {

    @NotNull
    private Long sectionId;

    @NotNull
    @Min(5)
    @Max(100)
    private Integer sizePercent;
}
