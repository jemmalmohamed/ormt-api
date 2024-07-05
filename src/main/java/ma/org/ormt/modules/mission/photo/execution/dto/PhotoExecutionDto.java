package ma.org.ormt.modules.mission.photo.execution.dto;

import org.locationtech.jts.geom.Polygon;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.BaseDto;
import ma.org.ormt.core.geometry.serializer.PolygonSerializer;

@Setter
@Getter
@Schema(name = "PhotoExecution")
@RequiredArgsConstructor
public class PhotoExecutionDto extends BaseDto {

    @JsonSerialize(using = PolygonSerializer.class)
    private Polygon emprise;

    private String observation;

    private String date;

    private String bobine;

}