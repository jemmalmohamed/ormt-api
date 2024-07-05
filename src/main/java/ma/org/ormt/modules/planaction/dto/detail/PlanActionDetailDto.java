package ma.org.ormt.modules.planaction.dto.detail;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ormt.modules.mission.dto.summary.MissionSummaryDto;
import ma.org.ormt.modules.planaction.dto.PlanActionDto;

@Setter
@Getter
@Schema(name = "PlanActionDetailDto")
@RequiredArgsConstructor
@JsonIgnoreProperties(value = { "plan_action.id" }, allowGetters = true)
public class PlanActionDetailDto extends PlanActionDto {

    List<MissionSummaryDto> missions;

}
