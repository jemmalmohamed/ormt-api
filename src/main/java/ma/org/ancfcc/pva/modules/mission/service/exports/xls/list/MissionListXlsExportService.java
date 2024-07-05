package ma.org.ancfcc.pva.modules.mission.service.exports.xls.list;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.springframework.http.ResponseEntity;

import ma.org.ancfcc.pva.core.commun.rest.queries.QueryParams;
import ma.org.ancfcc.pva.modules.mission.dto.export.ExportMissionRequestDto;

public interface MissionListXlsExportService {

        ResponseEntity<byte[]> exportMissionList(ExportMissionRequestDto requestDto) throws IOException;

        void createMissionListTableXls(ExportMissionRequestDto requestDto, ByteArrayOutputStream outputStream)
                        throws IOException;

        void createMissionListTableXls(ExportMissionRequestDto requestDto, QueryParams queryParams,
                        ByteArrayOutputStream outputStream)
                        throws IOException;
}
