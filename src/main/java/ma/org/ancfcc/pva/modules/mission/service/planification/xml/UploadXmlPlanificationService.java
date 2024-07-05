package ma.org.ancfcc.pva.modules.mission.service.planification.xml;

import java.io.File;

import jakarta.xml.bind.JAXBException;
import ma.org.ancfcc.pva.modules.mission.models.Mission;

public interface UploadXmlPlanificationService {
    void uploadPlanificationXmlFile(Mission mission, File file) throws JAXBException;
}