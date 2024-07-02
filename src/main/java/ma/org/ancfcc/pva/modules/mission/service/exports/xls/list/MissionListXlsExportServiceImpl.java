package ma.org.ancfcc.pva.modules.mission.service.exports.xls.list;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ma.org.ancfcc.pva.core.commun.rest.queries.QueryParams;
import ma.org.ancfcc.pva.modules.mission.dto.export.ExportMissionRequestDto;
import ma.org.ancfcc.pva.modules.mission.dto.export.FieldXlsParams;
import ma.org.ancfcc.pva.modules.mission.models.Mission;
import ma.org.ancfcc.pva.modules.mission.service.MissionService;
import ma.org.ancfcc.pva.modules.objet.Objet;

@Service
@RequiredArgsConstructor
public class MissionListXlsExportServiceImpl implements MissionListXlsExportService {

    @Autowired
    private final MissionService missionService;

    private static final String DEFAULT_DATE_FORMAT = "dd/MM/yyyy";

    @Override
    public void createMissionListTableXls(ExportMissionRequestDto requestDto, ByteArrayOutputStream outputStream)
            throws IOException {
        List<Mission> missionList = missionService.findAll();
        generateWorkbookXlsFile(requestDto, missionList, outputStream);
    }

    private void generateWorkbookXlsFile(ExportMissionRequestDto requestDto, List<Mission> missionList,
            ByteArrayOutputStream outputStream) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("missions");
            createHeaderRow(sheet, requestDto.getFields());
            int rowNum = 1;
            for (Mission mission : missionList) {
                Row row = sheet.createRow(rowNum++);
                populateRowByMissionDetail(workbook, row, requestDto.getFields(), mission);
            }

            autoSizeColumns(sheet, requestDto.getFields());
            workbook.write(outputStream);
        }
    }

    public void populateRowByMissionDetail(Workbook workbook, Row row, List<FieldXlsParams> fields,
            Mission mission) {
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
                    createObjetCell(row, mission.getObjets(), field);
                    break;
                case "mission_superficie":
                    createSuperficieCell(row, mission.getSuperficie(), field);
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
                case "mission_bande_total":
                    createTotalBandeCell(workbook, row, mission, field);

                    break;
                case "bande_photo_plani_total":
                    createTotalBandePhotoPlanCell(workbook, row, mission, field);
                    break;

                default:
                    break;
            }
        }
    }

    private void createTotalBandePhotoPlanCell(Workbook workbook, Row row, Mission mission,
            FieldXlsParams field) {

        long totalBandePhotoPlan = missionService.countPhotoPlanificationsByMissionId(mission.getId());
        Cell cell = row.createCell(field.getValue().getIndex());
        cell.setCellValue(totalBandePhotoPlan);
        if (mission.getCapteur().getFormat().equals("matricielle") && totalBandePhotoPlan == 0) {
            CellStyle style = createForegroundColorStyle(workbook, IndexedColors.RED.getIndex());
            cell.setCellStyle(style);
        }
    }

    private void createTotalBandeCell(Workbook workbook, Row row, Mission mission, FieldXlsParams field) {
        int totalBande = mission.getBandes().size();
        Cell cell = row.createCell(field.getValue().getIndex());
        cell.setCellValue(totalBande);

        // Check if the mission has a date and the total bande count is 0
        if (totalBande == 0) {
            if (mission.getDatePva() != null) {
                CellStyle style = createForegroundColorStyle(workbook, IndexedColors.RED.getIndex());
                cell.setCellStyle(style);
            } else {
                CellStyle style = createForegroundColorStyle(workbook, IndexedColors.YELLOW.getIndex());
                cell.setCellStyle(style);
            }

        }
    }

    private CellStyle createForegroundColorStyle(Workbook workbook, Short colorIndex) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(colorIndex);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private void createHeaderRow(Sheet sheet, List<FieldXlsParams> fields) {
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < fields.size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(fields.get(i).getValue().getLabel().toUpperCase());
        }
        sheet.setAutoFilter(new CellRangeAddress(
                headerRow.getRowNum(), // start row
                headerRow.getRowNum(), // end row
                headerRow.getFirstCellNum(), // start column
                headerRow.getLastCellNum() - 1 // end column
        ));
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

    private void createObjetCell(Row row, Set<Objet> objets, FieldXlsParams field) {
        String objetString = objets.stream().map(Objet::getNom).reduce((a, b) -> a + " - " + b).orElse("");
        Cell cell = row.createCell(field.getValue().getIndex());
        cell.setCellValue(objetString);
    }

    private void createSuperficieCell(Row row, Double superficie, FieldXlsParams field) {
        if (superficie == null) {
            superficie = 0.0;
        }
        Cell cell = row.createCell(field.getValue().getIndex());
        cell.setCellValue(superficie);
    }

    @Override
    public void createMissionListTableXls(ExportMissionRequestDto requestDto, QueryParams queryParams,
            ByteArrayOutputStream outputStream) throws IOException {

        List<Mission> missionList = missionService.getEntityList(queryParams).getContent();
        generateWorkbookXlsFile(requestDto, missionList, outputStream);
    }

}
