package ma.org.ancfcc.pva.modules.mission.service.imports;

import java.io.File;
import java.io.IOException;
import java.util.List;

import ma.org.ancfcc.pva.core.commun.base.service.BaseService;
import ma.org.ancfcc.pva.modules.mission.Mission;

public interface MissionImportService extends BaseService<Mission> {

        public void importMissionFromShapefile(List<File> shapefileComponents, Integer srid) throws IOException;

}