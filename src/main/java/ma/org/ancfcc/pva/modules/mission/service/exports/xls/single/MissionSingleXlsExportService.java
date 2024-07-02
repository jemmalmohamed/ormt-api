package ma.org.ancfcc.pva.modules.mission.service.exports.xls.single;

import java.io.IOException;

import org.springframework.http.ResponseEntity;

import ma.org.ancfcc.pva.modules.mission.dto.export.ExportMissionRequestDto;

public interface MissionSingleXlsExportService {

        public ResponseEntity<byte[]> exportSingleMission(ExportMissionRequestDto requestDto)
                        throws IOException;

}
