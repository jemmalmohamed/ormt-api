package ma.org.ormt.modules.mission.photo.planification.dto;

import org.locationtech.jts.geom.Point;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.BaseDto;
import ma.org.ormt.core.geometry.serializer.PointSerializer;

@Setter
@Getter
@Schema(name = "PhotoPlanification")
@RequiredArgsConstructor
public class PhotoPlanificationDto extends BaseDto {

    private String nom;

    private String label;

    @JsonSerialize(using = PointSerializer.class)
    private Point center;

    private String observation;

}