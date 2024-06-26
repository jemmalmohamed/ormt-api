package ma.org.ancfcc.pva.modules.objet.dto.mission;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

import lombok.Setter;
import ma.org.ancfcc.pva.core.commun.base.dto.BaseDto;
import ma.org.ancfcc.pva.core.commun.base.dto.Dto;
import ma.org.ancfcc.pva.modules.mission.Mission;

@Setter
@Getter
@Schema(name = "Objet")
@JsonIgnoreProperties(value = { "objet.id" }, allowGetters = true)
@AllArgsConstructor
@NoArgsConstructor
public class ObjetMissionDto extends Dto {

    private String nom;

    private String code;
}