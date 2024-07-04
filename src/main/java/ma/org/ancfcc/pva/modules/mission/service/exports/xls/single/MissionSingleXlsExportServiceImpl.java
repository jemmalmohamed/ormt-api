package ma.org.ancfcc.pva.modules.mission.service.exports.xls.single;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ma.org.ancfcc.pva.core.commun.rest.queries.QueryParams;
import ma.org.ancfcc.pva.modules.mission.models.Mission;
import ma.org.ancfcc.pva.modules.mission.service.MissionService;
import ma.org.ancfcc.pva.modules.mission.service.exports.xls.single.bande.BandeTableXlsGenerator;
import ma.org.ancfcc.pva.modules.mission.service.exports.xls.single.informations.MissionInformationsXlsGenerator;

@Service
@RequiredArgsConstructor
public class MissionSingleXlsExportServiceImpl implements MissionSingleXlsExportService {

    @Autowired
    private final MissionService missionService;

    @Autowired
    private final MissionInformationsXlsGenerator missionInformationsXlsGenerator;

    @Autowired
    private final BandeTableXlsGenerator bandeTableXlsGenerator;

    @Override
    public ResponseEntity<byte[]> exportSingleMissionBySheet() throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            List<Mission> missionList = missionService.findAll();
            createSingleSheetMission(missionList, outputStream);
            return exportExcelFormat(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<byte[]> exportSingleMissionBySheet(QueryParams queryParams) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            List<Mission> missionList = missionService.getEntityList(queryParams).getContent();
            createSingleSheetMission(missionList, outputStream);
            return exportExcelFormat(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void createSingleSheetMission(List<Mission> missionList, ByteArrayOutputStream outputStream)
            throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            for (Mission mission : missionList) {
                Sheet sheet = workbook.createSheet(mission.getCode());
                missionInformationsXlsGenerator.createMissionInformationTable(sheet, mission, workbook);
                bandeTableXlsGenerator.createBandesTable(sheet, mission, workbook);

            }
            workbook.write(outputStream);
        }
    }

    private ResponseEntity<byte[]> exportExcelFormat(ByteArrayOutputStream outputStream) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.ms-excel"));
        headers.setContentDispositionFormData("attachment", "missions.xlsx");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);

    }

}
