package ma.org.ancfcc.pva.modules.mission.service.exports.xls.single;

import java.io.IOException;

import org.springframework.http.ResponseEntity;

import ma.org.ancfcc.pva.core.commun.rest.queries.QueryParams;

public interface MissionSingleXlsExportService {

        public ResponseEntity<byte[]> exportSingleMissionBySheet()
                        throws IOException;

        public ResponseEntity<byte[]> exportSingleMissionBySheet(QueryParams requestParam)
                        throws IOException;

}
