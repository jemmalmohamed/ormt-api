package ma.org.ancfcc.pva.modules.mission.dto.export;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Schema(name = "ExportMissionRequest")
@RequiredArgsConstructor
@JsonIgnoreProperties(value = { "mission.id" }, allowGetters = true)
public class ExportMissionRequestDto {

    @NotBlank(message = "Selection format d'export")
    private String format;

    private List<FieldXlsParams> fields;

    private boolean singleSheet;

}