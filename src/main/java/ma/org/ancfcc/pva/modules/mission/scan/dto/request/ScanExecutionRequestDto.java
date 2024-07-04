package ma.org.ancfcc.pva.modules.mission.scan.dto.request;

import java.time.LocalDate;

import org.locationtech.jts.geom.Polygon;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ancfcc.pva.core.commun.base.dto.BaseDto;

@Setter
@Getter
@Schema(name = "ScanExecution")
@RequiredArgsConstructor
public class ScanExecutionRequestDto extends BaseDto {

    private Polygon polygon;

    private String nom;

    private LocalDate datePva;

    private String observation;

}