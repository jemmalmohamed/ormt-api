package ma.org.ancfcc.pva.modules.mission.service.exports.xls;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import ma.org.ancfcc.pva.modules.mission.Mission;
import ma.org.ancfcc.pva.modules.mission.dto.detail.MissionDetailDto;
import ma.org.ancfcc.pva.modules.mission.dto.detail.MissionDetailDtoMapper;
import ma.org.ancfcc.pva.modules.mission.dto.export.ExportMissionRequestDto;
import ma.org.ancfcc.pva.modules.mission.dto.export.FieldXlsParams;
import ma.org.ancfcc.pva.modules.mission.service.MissionService;
import ma.org.ancfcc.pva.modules.objet.dto.summary.ObjetSummaryDto;

@Service
public class MissionXlsExportServiceImpl implements MissionXlsExportService {

    @Autowired
    private MissionService missionService;

    @Autowired
    private MissionDetailDtoMapper missionDetailMapper;

    private static final String DEFAULT_DATE_FORMAT = "dd/MM/yyyy";

    @Override
    public ResponseEntity<byte[]> exportMissionList(ExportMissionRequestDto requestDto) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            createMissionXls(requestDto, outputStream);
            return exportExcelFormat(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void createMissionXls(ExportMissionRequestDto requestDto, ByteArrayOutputStream outputStream)
            throws IOException {
        List<Mission> missionList = missionService.findAll();
        List<MissionDetailDto> missionDetailDtos = missionDetailMapper.mapToDto(missionList);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("missions");
            createHeaderRow(sheet, requestDto.getFields());
            int rowNum = 1;
            for (MissionDetailDto mission : missionDetailDtos) {
                Row row = sheet.createRow(rowNum++);
                populateRowByMissionDetail(workbook, row, requestDto.getFields(), mission);
            }

            autoSizeColumns(sheet, requestDto.getFields());
            workbook.write(outputStream);
        }

    }

    public void populateRowByMissionDetail(Workbook workbook, Row row, List<FieldXlsParams> fields,
            MissionDetailDto mission) {
        for (FieldXlsParams field : fields) {
            int index = field.getValue().getIndex();

            Cell cell = row.createCell(index);
            switch (field.getKey()) {
                case "mission_code":
                    cell.setCellValue(mission.getCode().toUpperCase());
                    break;
                case "mission_nom":
                    cell.setCellValue(mission.getNom());
                    break;
                case "mission_date_pva":
                    createDatePvaCell(workbook, row, mission.getDatePva(), field);
                    break;
                case "mission_objet":
                    createObjetCell(workbook, row, mission.getObjets(), field);
                    break;
                case "mission_superficie":
                    createSuperficieCell(workbook, row, mission.getSuperficie(), field);
                    break;
                case "mission_organisme":
                    cell.setCellValue(mission.getOrganisme().getNom());
                    break;
                case "mission_capteur":
                    cell.setCellValue(mission.getCapteur().getNom());
                    break;
                case "pa_nom":
                    cell.setCellValue(mission.getPlanAction().getNom());
                    break;
                default:
                    break;
            }
        }
    }

    private void createHeaderRow(Sheet sheet, List<FieldXlsParams> fields) {
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < fields.size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(fields.get(i).getValue().getLabel().toUpperCase());
        }
    }

    private void autoSizeColumns(Sheet sheet, List<FieldXlsParams> fields) {
        for (int i = 0; i < fields.size(); i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void createDatePvaCell(Workbook workbook, Row row, LocalDate dataPva, FieldXlsParams field) {

        String dateFormat = Optional.ofNullable(field.getValue().getDateFormat()).orElse(DEFAULT_DATE_FORMAT);
        CreationHelper createHelper = workbook.getCreationHelper();
        // Définition du format de date
        DataFormat format = createHelper.createDataFormat();
        CellStyle dateStyle = workbook.createCellStyle();
        dateStyle.setDataFormat(format.getFormat(dateFormat));
        Cell dateCell = row.createCell(field.getValue().getIndex());
        dateCell.setCellValue(dataPva);
        dateCell.setCellStyle(dateStyle);
    }

    private void createObjetCell(Workbook workbook, Row row, List<ObjetSummaryDto> objets, FieldXlsParams field) {
        String objetString = objets.stream().map(ObjetSummaryDto::getNom).reduce((a, b) -> a + " - " + b).orElse("");
        Cell cell = row.createCell(field.getValue().getIndex());
        cell.setCellValue(objetString);
    }

    private void createSuperficieCell(Workbook workbook, Row row, Double superficie, FieldXlsParams field) {
        if (superficie == null) {
            superficie = 0.0;
        }
        Cell cell = row.createCell(field.getValue().getIndex());
        cell.setCellValue(superficie);
    }

    private ResponseEntity<byte[]> exportExcelFormat(ByteArrayOutputStream outputStream) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.ms-excel"));
        headers.setContentDispositionFormData("attachment", "missions.xlsx");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);

    }

}
