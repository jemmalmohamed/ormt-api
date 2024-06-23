package ma.org.ancfcc.pva.modules.mission.dto.organisme;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Schema(name = "MissionOrganismeDetailDto")
@RequiredArgsConstructor
@JsonIgnoreProperties(value = { "mission.id" }, allowGetters = true)
public class MissionOrganismeDto {

    private UUID id;

    private String nom;

    private String secteur;
}
