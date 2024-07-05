package ma.org.ormt.modules.mission.service.planification;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.xml.bind.JAXBException;
import ma.org.ormt.core.commun.base.service.BaseService;
import ma.org.ormt.modules.mission.models.Mission;

public interface MissionPlanificationService extends BaseService<Mission> {

    void uploadPlanificationFile(Long id, MultipartFile multipartFile) throws IOException, JAXBException;

    void uploadPlanificationFile(Long id, File file) throws IOException, JAXBException;

    void uploadPlanificationFile(Mission mission, File file) throws IOException, JAXBException;

    void removeMissionPlanification(Long id);

    void uploadAnalogiqueEOShapefile(List<File> shapefileComponentFiles, Integer srid) throws IOException;

}