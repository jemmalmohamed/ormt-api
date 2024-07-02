package ma.org.ancfcc.pva.modules.mission.service.exports.xls.single.informations;

import java.time.format.DateTimeFormatter;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ma.org.ancfcc.pva.core.utilities.XlsUtils;
import ma.org.ancfcc.pva.modules.capteur.enums.CapteurFormat;
import ma.org.ancfcc.pva.modules.mission.models.Mission;
import ma.org.ancfcc.pva.modules.mission.service.MissionService;
import ma.org.ancfcc.pva.modules.objet.Objet;

@Service
@RequiredArgsConstructor
public class MissionInformationsXlsGeneratorImpl implements MissionInformationsXlsGenerator {

    private static final String DEFAULT_EMPTY = "********";
    private static final String DEFAULT_DATE_FORMAT = "dd/MM/yyyy";

    @Autowired
    private final MissionService missionService;

    @Override
    public void createMissionInformationTable(Sheet sheet, Mission mission, Workbook workbook) {
        int rowNum = 0;
        Row row = sheet.createRow(rowNum++);
        sheet.setColumnWidth(0, 30 * 256);
        sheet.setColumnWidth(1, 30 * 256);
        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerCellStyle.setFont(headerFont);
        row.createCell(0).setCellValue("FICHE MISSION");
        row.getCell(0).setCellStyle(headerCellStyle);
        sheet.createRow(rowNum++);

        String[] headers = {
                "exercice",
                "code",
                "nom",
                "objet",
                "superficie",
                "date pva",
                "camera",
                "planifié",
                "total axes planifiés",
                "total photo planifiées",
        };

        for (int i = 0; i < headers.length; i++) {
            row = sheet.createRow(rowNum++);

            setHeaderCell(row, 0, headers[i], workbook);
            CellStyle style = createValueStyle(workbook);
            // Use custom methods for different types of details, passing the mission object
            switch (headers[i]) {
                case "exercice":
                    setPlanActionCell(row, 1, mission, style);
                    break;
                case "code":
                    setCodeCell(row, 1, mission, style);
                    break;
                case "nom":
                    setNomCell(row, 1, mission, style);
                    break;
                case "objet":
                    setObjetCell(row, 1, mission, style);
                    break;
                case "superficie":
                    setSuperficieCell(row, 1, mission, style);
                    break;
                case "date pva":
                    setDatePvaCell(row, 1, mission, style);
                    break;

                case "camera":
                    setCapteurCell(row, 1, mission, style);
                    break;
                case "planifié":
                    setPlanifieStatusCell(row, 1, mission, style);
                    break;
                case "total axes planifiés":
                    setTotalBandesPlanifiesCell(row, 1, mission, workbook, style);
                    break;
                case "total photo planifiées":
                    setTotalPhotoPlanifieesCell(row, 1, mission, style);
                    break;
                default:
                    setCellWithStyle(row, 1, DEFAULT_EMPTY, style);
                    break;
            }
        }
    }

    private void setPlanActionCell(Row row, int cellNum, Mission mission, CellStyle style) {
        Cell cell = row.createCell(cellNum);
        cell.setCellValue(mission.getPlanAction().getNom().toUpperCase());
        cell.setCellStyle(style);
    }

    private void setObjetCell(Row row, int cellNum, Mission mission, CellStyle style) {
        Cell cell = row.createCell(cellNum);
        String objetString = mission.getObjets().stream().map(Objet::getNom).reduce((a, b) -> a + " - " + b).orElse("");
        cell.setCellValue(objetString);
        cell.setCellStyle(style);
    }

    private void setTotalPhotoPlanifieesCell(Row row, int cellNum, Mission mission,
            CellStyle style) {
        Cell cell = row.createCell(cellNum);
        long totalBandePhotoPlan = missionService.countPhotoPlanificationsByMissionId(mission.getId());
        cell.setCellValue(mission.getCapteur().getFormat().equals(CapteurFormat.MATRICIELLE.getDescription())
                ? String.valueOf(totalBandePhotoPlan)
                : DEFAULT_EMPTY);

        cell.setCellStyle(style);
    }

    private void setCellWithStyle(Row row, int cellNum, String value, CellStyle style) {
        Cell cell = row.createCell(cellNum);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private void setDatePvaCell(Row row, int cellNum, Mission mission, CellStyle style) {
        Cell cell = row.createCell(cellNum);
        cell.setCellValue(mission.getDatePva() != null
                ? mission.getDatePva().format(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT))
                : DEFAULT_EMPTY);
        cell.setCellStyle(style);
    }

    private void setCodeCell(Row row, int cellNum, Mission mission, CellStyle style) {

        Cell cell = row.createCell(cellNum);
        cell.setCellValue(mission.getCode().toUpperCase());
        cell.setCellStyle(style);
    }

    private void setNomCell(Row row, int cellNum, Mission mission, CellStyle style) {
        Cell cell = row.createCell(cellNum);
        cell.setCellValue(mission.getNom().toUpperCase());
        cell.setCellStyle(style);
    }

    private void setSuperficieCell(Row row, int cellNum, Mission mission, CellStyle style) {
        Cell cell = row.createCell(cellNum);
        cell.setCellValue(mission.getSuperficie());
        cell.setCellStyle(style);
    }

    private void setPlanifieStatusCell(Row row, int cellNum, Mission mission, CellStyle style) {
        Cell cell = row.createCell(cellNum);
        String status = !mission.getBandes().isEmpty() ? "OUI" : "NON";
        cell.setCellValue(status);
        cell.setCellStyle(style);
    }

    private void setCapteurCell(Row row, int cellNum, Mission mission, CellStyle style) {
        Cell cell = row.createCell(cellNum);
        cell.setCellValue(mission.getCapteur().getNom().toUpperCase());
        cell.setCellStyle(style);
    }

    private void setHeaderCell(Row row, int cellNum, String value, Workbook workbook) {
        Cell cell = row.createCell(cellNum);
        cell.setCellValue(value.toUpperCase());
        cell.setCellStyle(createHeaderStyle(workbook));
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        return style;
    }

    public void setTotalBandesPlanifiesCell(Row row, int index, Mission mission, Workbook workbook,
            CellStyle style) {
        int totalBande = mission.getBandes().size();

        Cell cell = row.createCell(index);
        cell.setCellStyle(style);
        cell.setCellValue(totalBande);
        if (totalBande == 0) {
            if (mission.getDatePva() != null) {
                style = workbook.createCellStyle();
                style.setFillForegroundColor(IndexedColors.RED.getIndex());
                style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                style.setAlignment(HorizontalAlignment.CENTER);
                cell.setCellStyle(style);
            } else {
                style = workbook.createCellStyle();
                style.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
                style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                style.setAlignment(HorizontalAlignment.CENTER);
                cell.setCellStyle(style);
            }

        }
    }

    private CellStyle createValueStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        return style;
    }
}
