package ma.org.ormt.modules.dashboard.tbd.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TbdWidgetRowDto {

    private Long id;
    private Integer ordre;
    private Integer sizePercent;
    private Integer heightPx;
    private List<TbdWidgetDto> widgets;
}
