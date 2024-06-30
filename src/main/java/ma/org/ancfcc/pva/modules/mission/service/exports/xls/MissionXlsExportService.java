package ma.org.ancfcc.pva.modules.mission.service.exports.xls;

import java.io.IOException;

import org.springframework.http.ResponseEntity;

import ma.org.ancfcc.pva.modules.mission.dto.export.ExportMissionRequestDto;

public interface MissionXlsExportService {

        public ResponseEntity<byte[]> exportMissionList(ExportMissionRequestDto requestDto)
                        throws IOException;

}
