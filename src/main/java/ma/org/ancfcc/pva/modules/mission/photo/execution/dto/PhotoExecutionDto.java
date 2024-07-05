package ma.org.ancfcc.pva.modules.mission.photo.execution.dto;

import org.locationtech.jts.geom.Polygon;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ancfcc.pva.core.commun.base.dto.BaseDto;
import ma.org.ancfcc.pva.core.geometry.serializer.PolygonSerializer;

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