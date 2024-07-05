package ma.org.ormt.modules.mission.photo.orientation.dto;

import org.locationtech.jts.geom.Point;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.BaseDto;
import ma.org.ormt.core.geometry.serializer.PolygonSerializer;

@Setter
@Getter
@Schema(name = "PhotoOrientation")
@RequiredArgsConstructor
public class PhotoOrientationDto extends BaseDto {

    @JsonSerialize(using = PolygonSerializer.class)
    private Point centre;

    private String observation;

    private Float omega;

    private Float phi;

    private Float kappa;

    private String geoidModel;

    private Float altitude;

    private Float tempsGps;

}