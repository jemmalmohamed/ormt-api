package ma.org.ancfcc.pva.modules.mission.photo.planification.dto.request;

import org.locationtech.jts.geom.Point;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ancfcc.pva.core.commun.base.dto.BaseDto;

@Setter
@Getter
@Schema(name = "PhotoPlanification")
@RequiredArgsConstructor
public class PhotoPlanificationRequestDto extends BaseDto {

    private Point center;

    private String observation;

}