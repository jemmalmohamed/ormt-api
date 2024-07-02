package ma.org.ancfcc.pva.modules.mission.service.planification;

import java.io.File;

import org.springframework.stereotype.Service;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import ma.org.ancfcc.pva.modules.mission.service.planification.parser.xml.ListViewTable;

@Service
public class PlanificationParserService {

    public ListViewTable parsePlanificationXml(File xmlFile) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(ListViewTable.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        return (ListViewTable) jaxbUnmarshaller.unmarshal(xmlFile);

    }
}
