package ma.org.ormt.modules.observatoire.dtos.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.org.ormt.core.commun.base.dto.Dto;
import ma.org.ormt.modules.observatoire.dtos.ObservatoireActionCardDto;
import ma.org.ormt.modules.observatoire.dtos.ObservatoirePillarDto;
import ma.org.ormt.modules.observatoire.dtos.ObservatoireTeamMemberDto;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ObservatoirePageContentRequestDto extends Dto {

    @NotBlank(message = "Ce champ est requis.")
    private String kicker;

    @NotBlank(message = "Ce champ est requis.")
    private String badgeTitle;

    @NotBlank(message = "Ce champ est requis.")
    private String badgeSubtitle;

    @NotBlank(message = "Ce champ est requis.")
    private String heroTagline;

    @NotBlank(message = "Ce champ est requis.")
    private String introText;

    @NotBlank(message = "Ce champ est requis.")
    private String visionText;

    @NotBlank(message = "Ce champ est requis.")
    private String missionText;

    @NotBlank(message = "Ce champ est requis.")
    private String partnershipText;

    @NotEmpty(message = "Au moins un objectif est requis.")
    private List<String> objectives;

    @NotEmpty(message = "Au moins un domaine d'activite est requis.")
    private List<ObservatoirePillarDto> pillars;

    @NotEmpty(message = "Au moins un membre d'equipe est requis.")
    private List<ObservatoireTeamMemberDto> team;

    @NotEmpty(message = "Au moins un acces rapide est requis.")
    private List<ObservatoireActionCardDto> actions;

    private Boolean actif;

    private Boolean published;
}