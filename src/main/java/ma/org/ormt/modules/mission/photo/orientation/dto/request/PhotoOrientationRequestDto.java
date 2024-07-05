package ma.org.ormt.modules.mission.photo.orientation.dto.request;

import org.locationtech.jts.geom.Point;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.BaseDto;

@Setter
@Getter
@Schema(name = "PhotoOrientation")
@RequiredArgsConstructor
public class PhotoOrientationRequestDto extends BaseDto {

    private Point center;

    private String observation;

    private Float omega;

    private Float phi;

    private Float kappa;

    private String geoidModel;

    private Float altitude;

    private Float tempsGps;

}