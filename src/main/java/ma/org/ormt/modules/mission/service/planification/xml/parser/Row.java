package ma.org.ormt.modules.mission.service.planification.xml.parser;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlAccessType;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class Row {

    @XmlAttribute(name = "Selected")
    private boolean selected;

    @XmlElement(name = "Item")
    private List<Item> items;

    public String getSensorType() {
        return items.get(0).getValue();
    }

    public String getStatus() {
        return items.get(1).getValue();
    }

    public String getPlanLineLabel() {
        return items.get(2).getValue();
    }

    public String eventLabel() {
        return items.get(3).getValue();
    }

    public String getPosition() {
        return items.get(4).getValue();
    }

    public EventType getType() {
        return EventType.valueOf(items.get(5).getValue().toUpperCase());
    }

}
