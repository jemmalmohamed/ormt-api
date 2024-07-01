package ma.org.ancfcc.pva.modules.mission.dto;

import java.time.LocalDate;
import java.util.List;

import org.locationtech.jts.geom.MultiPolygon;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.org.ancfcc.pva.core.commun.base.dto.BaseDto;
import ma.org.ancfcc.pva.core.geometry.serializer.MultiPolygonSerializer;
import ma.org.ancfcc.pva.modules.capteur.dto.summary.CapteurSummaryDto;
import ma.org.ancfcc.pva.modules.objet.dto.summary.ObjetSummaryDto;
import ma.org.ancfcc.pva.modules.organisme.dto.summary.OrganismeSummaryDto;
import ma.org.ancfcc.pva.modules.planaction.dto.summary.PlanActionSummaryDto;

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

    private Double superficie;

    private String description;

    @JsonSerialize(using = MultiPolygonSerializer.class)
    private MultiPolygon delimitation;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate datePva;

    private OrganismeSummaryDto organisme;

    private CapteurSummaryDto capteur;

    private PlanActionSummaryDto planAction;

    private List<ObjetSummaryDto> objets;
}