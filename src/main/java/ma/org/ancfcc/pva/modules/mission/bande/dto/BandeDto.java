package ma.org.ancfcc.pva.modules.mission.bande.dto;

import org.locationtech.jts.geom.LineString;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ancfcc.pva.core.commun.base.dto.BaseDto;
import ma.org.ancfcc.pva.core.geometry.serializer.LineStringSerializer;

@Setter
@Getter
@Schema(name = "Bande")
@RequiredArgsConstructor
public class BandeDto extends BaseDto {

    private String nom;

    private String label;

    private String commentaire;

    @JsonSerialize(using = LineStringSerializer.class)
    private LineString axePlanification;

}