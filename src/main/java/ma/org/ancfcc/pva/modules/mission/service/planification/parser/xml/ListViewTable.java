package ma.org.ancfcc.pva.modules.mission.service.planification.parser.xml;

import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter
@Getter
@XmlRootElement(name = "ListViewTable")
@XmlAccessorType(XmlAccessType.FIELD)
public class ListViewTable {

    @XmlElement(name = "Row")
    private List<Row> rows;

    public String getSensorType() {
        return rows.get(0).getSensorType();
    }

    public List<PlanLineXmlInfo> extractPlanLineInfo() {
        Map<String, PlanLineXmlInfo> flightLineInfoMap = new HashMap<>();
        List<PlanLineXmlInfo> result = new ArrayList<>();

        for (Row row : rows) {
            String flightLineLabel = row.getPlanLineLabel();
            String status = row.getStatus();
            EventType eventType = row.getType(); // Assuming getType() returns an EventType enum
            String position = row.getPosition();

            if ("Active".equals(status)) {
                PlanLineXmlInfo info = flightLineInfoMap.computeIfAbsent(flightLineLabel, k -> new PlanLineXmlInfo());
                info.setPlanLineLabel(flightLineLabel);
                info.setStatus(status);

                if ("Start".equals(eventType.getDescription())) {
                    info.setStartPosition(position);
                } else if ("End".equals(eventType.getDescription())) {
                    info.setEndPosition(position);
                }

                // Check if both start and end positions are set, then we have complete info
                if (info.getStartPosition() != null && info.getEndPosition() != null) {
                    result.add(info);
                    flightLineInfoMap.remove(flightLineLabel); // Remove to avoid duplicate info
                }
            }
        }
        return result;
    }

    public Map<String, List<PlanEventInfo>> extractPlanEventInfo() {
        Map<String, List<PlanEventInfo>> groupedResult = new HashMap<>();

        for (Row row : rows) {
            String status = row.getStatus();

            if ("Active".equals(status)) {
                PlanEventInfo info = new PlanEventInfo();
                info.setStatus(status);
                info.setPlanLineLabel(row.getPlanLineLabel());
                info.setPlanEventLabel(row.EventLabel());
                info.setPosition(row.getPosition());
                info.setEventType(row.getType());

                // Get the list of PlanEventInfo objects for this planLineLabel, or create a new
                // list if none exists
                List<PlanEventInfo> planEventInfoList = groupedResult.computeIfAbsent(row.getPlanLineLabel(),
                        k -> new ArrayList<>());

                // Add the new PlanEventInfo object to the list
                planEventInfoList.add(info);
            }
        }
        return groupedResult;
    }

}
