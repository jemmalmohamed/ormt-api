package ma.org.ancfcc.pva.modules.planaction.dto.detail;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ancfcc.pva.modules.planaction.dto.PlanActionDto;

@Setter
@Getter
@Schema(name = "PlanActionDetailDto")
@RequiredArgsConstructor
@JsonIgnoreProperties(value = { "plan_action.id" }, allowGetters = true)
public class PlanActionDetailDto extends PlanActionDto {

}
