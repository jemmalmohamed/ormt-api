package ma.org.ancfcc.pva.modules.planaction.dto.summary;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ancfcc.pva.core.commun.base.dto.Dto;

@Setter
@Getter
@Schema(name = "MissionPlanActionDetailDto")
@RequiredArgsConstructor
@JsonIgnoreProperties(value = { "mission.id" }, allowGetters = true)
public class PlanActionSummaryDto extends Dto {

    private String nom;

    private String description;

}
