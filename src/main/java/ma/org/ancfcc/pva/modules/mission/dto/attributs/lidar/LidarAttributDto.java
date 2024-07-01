package ma.org.ancfcc.pva.modules.mission.dto.attributs.lidar;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ancfcc.pva.core.commun.base.dto.Dto;

@Setter
@Getter
@Schema(name = "LidarAttributDto")
@RequiredArgsConstructor
public class LidarAttributDto extends Dto {

    private Float densite;

}