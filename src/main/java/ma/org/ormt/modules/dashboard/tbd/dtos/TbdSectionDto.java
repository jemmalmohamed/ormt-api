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
public class TbdSectionDto {

    private Long id;
    private String label;
    private Integer ordre;
    private Integer sizePercent;
    private List<TbdWidgetRowDto> rows;
}
