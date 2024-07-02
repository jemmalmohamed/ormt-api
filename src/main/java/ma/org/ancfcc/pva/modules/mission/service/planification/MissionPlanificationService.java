package ma.org.ancfcc.pva.modules.mission.service.planification;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import jakarta.xml.bind.JAXBException;
import ma.org.ancfcc.pva.core.commun.base.service.BaseService;
import ma.org.ancfcc.pva.modules.mission.models.Mission;

public interface MissionPlanificationService extends BaseService<Mission> {

    void uploadPlanificationFile(UUID id, MultipartFile multipartFile) throws IOException, JAXBException;

    void uploadPlanificationFile(UUID id, File file) throws IOException, JAXBException;

    void uploadPlanificationFile(Mission mission, File file) throws IOException, JAXBException;

    void removeMissionPlanification(UUID id);

    void uploadAnalogiqueShapefile(List<File> shapefileComponentFiles, Integer srid) throws IOException;

}