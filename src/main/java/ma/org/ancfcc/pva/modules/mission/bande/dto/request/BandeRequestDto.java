package ma.org.ancfcc.pva.modules.mission.bande.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ma.org.ancfcc.pva.core.commun.base.dto.Dto;
import ma.org.ancfcc.pva.modules.mission.dto.summary.MissionSummaryDto;

@Setter
@Getter
@Builder
@AllArgsConstructor
@Schema(name = "Bande")
@RequiredArgsConstructor
public class BandeRequestDto extends Dto {

    @NotBlank(message = "Ce champ est requis.")
    private String nom;

    private String commentaire;

    @NotNull(message = "Sélectionnez une mission")
    private MissionSummaryDto mission;

}