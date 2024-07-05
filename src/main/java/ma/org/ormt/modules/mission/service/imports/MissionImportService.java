package ma.org.ormt.modules.mission.service.imports;

import java.io.File;
import java.io.IOException;
import java.util.List;

import ma.org.ormt.core.commun.base.service.BaseService;
import ma.org.ormt.modules.mission.models.Mission;

public interface MissionImportService extends BaseService<Mission> {

        public void importMissionFromShapefile(List<File> shapefileComponents, Integer srid) throws IOException;

}