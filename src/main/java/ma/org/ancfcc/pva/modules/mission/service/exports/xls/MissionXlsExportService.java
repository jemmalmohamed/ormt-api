package ma.org.ancfcc.pva.modules.mission.service.exports.xls;

import java.io.IOException;

import org.springframework.http.ResponseEntity;

import ma.org.ancfcc.pva.core.commun.rest.queries.QueryParams;
import ma.org.ancfcc.pva.modules.mission.dto.export.ExportMissionRequestDto;

public interface MissionXlsExportService {

        public ResponseEntity<byte[]> exportMissionList(ExportMissionRequestDto requestDto)
                        throws IOException;

        public ResponseEntity<byte[]> exportSingleMissionBySheet(ExportMissionRequestDto requestDto)
                        throws IOException;

        public ResponseEntity<byte[]> exportSingleMissionBySheet(ExportMissionRequestDto requestDto,
                        QueryParams requestParam)
                        throws IOException;

}
