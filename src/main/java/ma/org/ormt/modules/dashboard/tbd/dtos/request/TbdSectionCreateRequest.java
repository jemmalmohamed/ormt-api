package ma.org.ormt.modules.dashboard.tbd.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TbdSectionCreateRequest {

    private String label;

    private Integer ordre;

    private Integer sizePercent;
}
