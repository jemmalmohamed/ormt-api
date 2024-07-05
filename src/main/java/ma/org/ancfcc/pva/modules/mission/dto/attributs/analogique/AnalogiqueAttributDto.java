package ma.org.ancfcc.pva.modules.mission.dto.attributs.analogique;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ancfcc.pva.core.commun.base.dto.Dto;

@Setter
@Getter
@Schema(name = "AnalogiqueAttributDto")
@RequiredArgsConstructor
public class AnalogiqueAttributDto extends Dto {

    private Long echelle;

}