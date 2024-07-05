package ma.org.ormt.modules.mission.service.planification.xml;

import java.io.File;

import jakarta.xml.bind.JAXBException;
import ma.org.ormt.modules.mission.models.Mission;

public interface UploadXmlPlanificationService {
    void uploadPlanificationXmlFile(Mission mission, File file) throws JAXBException;
}