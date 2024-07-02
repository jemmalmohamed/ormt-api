package ma.org.ancfcc.pva.modules.mission.service.exports.xls;

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
import ma.org.ancfcc.pva.core.utilities.XlsUtils;
import ma.org.ancfcc.pva.modules.mission.dto.export.ExportMissionRequestDto;
import ma.org.ancfcc.pva.modules.mission.models.Mission;
import ma.org.ancfcc.pva.modules.mission.service.MissionService;
import ma.org.ancfcc.pva.modules.mission.service.exports.xls.list.MissionListXlsExportService;
import ma.org.ancfcc.pva.modules.mission.service.exports.xls.single.bande.BandeTableXlsGenerator;
import ma.org.ancfcc.pva.modules.mission.service.exports.xls.single.informations.MissionInformationsXlsGenerator;

@Service
@RequiredArgsConstructor
public class MissionXlsExportServiceImpl implements MissionXlsExportService {

    @Autowired
    private final MissionService missionService;

    @Autowired
    private final MissionInformationsXlsGenerator missionInformationsXlsGenerator;

    @Autowired
    private final MissionListXlsExportService missionListXlsExportService;

    @Autowired
    private final BandeTableXlsGenerator bandeTableXlsGenerator;

    public void createSingleSheetMission(ExportMissionRequestDto requestDto, ByteArrayOutputStream outputStream)
            throws IOException {
        List<Mission> missionList = missionService.findAll();

        try (Workbook workbook = new XSSFWorkbook()) {

            for (Mission mission : missionList) {
                Sheet sheet = workbook.createSheet(mission.getCode());
                missionInformationsXlsGenerator.createMissionInformationTable(sheet, mission, workbook);
                bandeTableXlsGenerator.createBandesTable(sheet, mission, workbook);

            }

            workbook.write(outputStream);
        }

    }

    public void createSingleSheetMission(ExportMissionRequestDto requestDto, QueryParams queryParams,
            ByteArrayOutputStream outputStream)
            throws IOException {
        List<Mission> missionList = missionService.getEntityList(queryParams).getContent();

        try (Workbook workbook = new XSSFWorkbook()) {

            for (Mission mission : missionList) {
                Sheet sheet = workbook.createSheet(mission.getCode());
                missionInformationsXlsGenerator.createMissionInformationTable(sheet, mission, workbook);
                bandeTableXlsGenerator.createBandesTable(sheet, mission, workbook);

            }

            workbook.write(outputStream);
        }

    }

    @Override
    public ResponseEntity<byte[]> exportSingleMissionBySheet(ExportMissionRequestDto requestDto) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            createSingleSheetMission(requestDto, outputStream);
            return exportExcelFormat(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<byte[]> exportExcelFormat(ByteArrayOutputStream outputStream) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.ms-excel"));
        headers.setContentDispositionFormData("attachment", "missions.xlsx");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);

    }

    @Override
    public ResponseEntity<byte[]> exportMissionList(ExportMissionRequestDto requestDto) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            missionListXlsExportService.createMissionListTableXls(requestDto, outputStream);
            return XlsUtils.exportExcelFormat(outputStream, "missions");
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<byte[]> exportSingleMissionBySheet(ExportMissionRequestDto requestDto,
            QueryParams requestParam) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            createSingleSheetMission(requestDto, requestParam, outputStream);
            return XlsUtils.exportExcelFormat(outputStream, "missions");
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

}
