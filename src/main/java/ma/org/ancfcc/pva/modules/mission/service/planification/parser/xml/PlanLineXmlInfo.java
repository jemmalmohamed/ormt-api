package ma.org.ancfcc.pva.modules.mission.service.planification.parser.xml;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlanLineXmlInfo {
    private String planLineLabel;
    private String status;
    private String startPosition;
    private String endPosition;
    private EventType eventType;

}
