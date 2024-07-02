package ma.org.ancfcc.pva.modules.mission.service.planification.parser.xml;

import jakarta.xml.bind.annotation.XmlElement;

import jakarta.xml.bind.annotation.XmlAccessorType;
import lombok.Getter;
import lombok.Setter;
import jakarta.xml.bind.annotation.XmlAccessType;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class Item {

    @XmlElement(name = "Name")
    private String name;

    @XmlElement(name = "Value")
    private String value;

}
