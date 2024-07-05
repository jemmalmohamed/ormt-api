package ma.org.ancfcc.pva.modules.mission.scan.dto;

import java.time.LocalDate;

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
@Schema(name = "ScanExecution")
@RequiredArgsConstructor
public class ScanExecutionDto extends BaseDto {

    private String nom;

    private LocalDate datePva;

    private String observation;

    @JsonSerialize(using = PolygonSerializer.class)
    private Polygon emprise;

}