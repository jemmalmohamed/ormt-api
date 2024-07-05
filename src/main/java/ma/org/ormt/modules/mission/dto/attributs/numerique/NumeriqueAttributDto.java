package ma.org.ormt.modules.mission.dto.attributs.numerique;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.Dto;

@Setter
@Getter
@Schema(name = "NumeriqueAttributDto")
@RequiredArgsConstructor
public class NumeriqueAttributDto extends Dto {

    private Integer resolution;

}