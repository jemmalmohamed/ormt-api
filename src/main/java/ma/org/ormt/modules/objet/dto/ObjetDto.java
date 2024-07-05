package ma.org.ormt.modules.objet.dto;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.BaseDto;
import ma.org.ormt.modules.mission.dto.summary.MissionSummaryDto;

@Setter
@Getter
@Schema(name = "Objet")
@JsonIgnoreProperties(value = { "objet.id" }, allowGetters = true)
@AllArgsConstructor
@NoArgsConstructor
public class ObjetDto extends BaseDto {

    private String nom;

    private String description;

    private Set<MissionSummaryDto> missions;
}