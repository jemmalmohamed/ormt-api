package ma.org.ancfcc.pva.modules.mission.service.execution;

import java.io.File;
import java.io.IOException;
import java.util.List;

import ma.org.ancfcc.pva.core.commun.base.service.BaseService;
import ma.org.ancfcc.pva.modules.mission.models.Mission;

public interface MissionExecutionService extends BaseService<Mission> {

        public void uploadNumeriqueExecutionShapefile(Long missionId, List<File> shapefileComponents, String dateFolder,
                        Integer srid)
                        throws IOException;

        public void uploadNumeriqueEOShapefile(List<File> shapefileComponents,
                        Integer srid)
                        throws IOException;

}