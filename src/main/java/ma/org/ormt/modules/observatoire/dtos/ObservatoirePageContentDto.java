package ma.org.ormt.modules.observatoire.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ObservatoirePageContentDto {

    private Long id;

    private String kicker;

    private String badgeTitle;

    private String badgeSubtitle;

    private String heroTagline;

    private String introText;

    private String visionText;

    private String missionText;

    private String partnershipText;

    private List<String> objectives;

    private List<ObservatoirePillarDto> pillars;

    private List<ObservatoireTeamMemberDto> team;

    private List<ObservatoireActionCardDto> actions;

    private boolean actif;

    private boolean published;
}