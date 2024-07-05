package ma.org.ormt.modules.mission.dto;

import java.time.LocalDate;
import java.util.List;

import org.locationtech.jts.geom.MultiPolygon;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.BaseDto;
import ma.org.ormt.core.geometry.serializer.MultiPolygonSerializer;
import ma.org.ormt.modules.capteur.dto.summary.CapteurSummaryDto;
import ma.org.ormt.modules.mission.bande.dto.BandeDto;
import ma.org.ormt.modules.mission.dto.attributs.analogique.AnalogiqueAttributDto;
import ma.org.ormt.modules.mission.dto.attributs.lidar.LidarAttributDto;
import ma.org.ormt.modules.mission.dto.attributs.numerique.NumeriqueAttributDto;
import ma.org.ormt.modules.objet.dto.summary.ObjetSummaryDto;
import ma.org.ormt.modules.organisme.dto.summary.OrganismeSummaryDto;
import ma.org.ormt.modules.planaction.dto.summary.PlanActionSummaryDto;

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

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private NumeriqueAttributDto numeriqueAttributs;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private AnalogiqueAttributDto analogiqueAttributs;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LidarAttributDto lidarAttributs;

    private List<BandeDto> bandes;

}