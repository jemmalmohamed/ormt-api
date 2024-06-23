package ma.org.ancfcc.pva.modules.mission.dto;

import java.time.LocalDate;

import org.locationtech.jts.geom.MultiPolygon;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.org.ancfcc.pva.core.commun.base.dto.BaseDto;
import ma.org.ancfcc.pva.core.geometry.serializer.MultiPolygonSerializer;
import ma.org.ancfcc.pva.modules.mission.dto.organisme.MissionOrganismeDto;
import ma.org.ancfcc.pva.modules.mission.dto.planaction.MissionPlanActionDto;

@Setter
@Getter
@Schema(name = "Mission")
@JsonIgnoreProperties(value = { "mission.id" }, allowGetters = true)
@AllArgsConstructor
@NoArgsConstructor
public class MissionDto extends BaseDto {

    private String code;

    private String nom;

    private String etat;

    private Long superficie;

    private String description;

    // @JsonSerialize(using = MultiPolygonSerializer.class)
    // private MultiPolygon delimitation;

    private LocalDate datePva;

    private MissionOrganismeDto organisme;

    private MissionPlanActionDto planAction;
}