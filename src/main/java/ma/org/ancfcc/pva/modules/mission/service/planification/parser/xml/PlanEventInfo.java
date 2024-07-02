package ma.org.ancfcc.pva.modules.mission.service.planification.parser.xml;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlanEventInfo {

    private String planLineLabel;
    private String planEventLabel;
    private String status;
    private String position;
    private EventType eventType;

}
