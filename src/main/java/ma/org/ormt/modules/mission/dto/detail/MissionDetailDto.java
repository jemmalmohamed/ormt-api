package ma.org.ormt.modules.mission.dto.detail;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.modules.mission.dto.MissionDto;

@Setter
@Getter
@Schema(name = "MissionDetailDto")
@RequiredArgsConstructor
@JsonIgnoreProperties(value = { "mission.id" }, allowGetters = true)
public class MissionDetailDto extends MissionDto {

}
