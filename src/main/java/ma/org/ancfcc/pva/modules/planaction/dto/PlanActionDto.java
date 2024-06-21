package ma.org.ancfcc.pva.modules.planaction.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.org.ancfcc.pva.core.commun.base.dto.BaseDto;

@Setter
@Getter
@Schema(name = "PlanAction")
@JsonIgnoreProperties(value = { "planAction.id" }, allowGetters = true)
@AllArgsConstructor
@NoArgsConstructor
public class PlanActionDto extends BaseDto {

    private String designation;

    private String description;

    private LocalDateTime debutDate;

    private LocalDateTime finDate;
}