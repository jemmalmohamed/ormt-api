package ma.org.ancfcc.pva.modules.mission.photo.execution.dto.request;

import org.locationtech.jts.geom.Polygon;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ancfcc.pva.core.commun.base.dto.BaseDto;

@Setter
@Getter
@Schema(name = "PhotoExecution")
@RequiredArgsConstructor
public class PhotoExecutionRequestDto extends BaseDto {

    private Polygon center;

    private String observation;

    @NotBlank(message = "Ce champ est requis.")
    private String date;

    private String bobine;

}