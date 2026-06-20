package ma.org.ormt.modules.dashboard.tbd.dtos.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TbdSectionResizeRequest {

    @NotNull
    @Valid
    private List<TbdSectionSizeItem> items;
}
