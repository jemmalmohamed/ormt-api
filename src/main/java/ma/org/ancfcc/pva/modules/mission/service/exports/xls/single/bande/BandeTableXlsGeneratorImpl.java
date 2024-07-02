package ma.org.ancfcc.pva.modules.mission.service.exports.xls.single.bande;

import java.util.List;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ma.org.ancfcc.pva.core.utilities.XlsUtils;
import ma.org.ancfcc.pva.modules.capteur.enums.CapteurFormat;
import ma.org.ancfcc.pva.modules.mission.bande.Bande;
import ma.org.ancfcc.pva.modules.mission.models.Mission;

@Service
@RequiredArgsConstructor
public class BandeTableXlsGeneratorImpl implements BandeTableXlsGenerator {

    private static final String DEFAULT_EMPTY = "";

    @Override
    public void createBandesTable(Sheet sheet, Mission mission, Workbook workbook) {
        int rowNum = sheet.getLastRowNum() + 2; // Add a couple of rows gap

        String[] headers = {
                "Bande",
                "label",
                "Total photos planifiées",
                "total photos réalisées",
                "commentaire" };

        Row row = sheet.createRow(rowNum++);
        for (int i = 0; i < headers.length; i++) {
            setHeaderBandeTableCell(row, i, headers[i], workbook);
        }

        List<Bande> bandes = mission.getBandes();
        CellStyle valueStyle = createValueStyle(workbook);

        // Populate data rows
        for (Bande bande : bandes) {
            row = sheet.createRow(rowNum++);
            setBandeNumCell(row, 0, bande, valueStyle);
            setBandeLabelCell(row, 1, bande, valueStyle);
            setBandeTotalPhotosPlanifieesCell(row, 2, bande, workbook, valueStyle);
            setCommentaireCell(row, 3, bande, workbook, valueStyle);
        }

        // Auto-size columns for better readability
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void setCommentaireCell(Row row, int cellNum, Bande bande, Workbook workbook, CellStyle style) {
        Cell cell = row.createCell(cellNum);
        cell.setCellValue(bande.getCommentaire());
        cell.setCellStyle(style);
    }

    private void setBandeLabelCell(Row row, int cellNum, Bande bande, CellStyle style) {
        Cell cell = row.createCell(cellNum);
        cell.setCellValue(bande.getLabel().toUpperCase());
        cell.setCellStyle(style);
    }

    public void setHeaderBandeTableCell(Row row, int cellNum, String value, Workbook workbook) {
        Cell cell = row.createCell(cellNum);
        cell.setCellValue(value.toUpperCase());
        cell.setCellStyle(createHeaderBandeTableStyle(workbook));
    }

    public void setBandeTotalPhotosPlanifieesCell(Row row, int cellNum, Bande bande, Workbook workbook,
            CellStyle style) {

        long totalBandePhotoPlan = bande.getPhotoPlanifications().size();
        Cell cell = row.createCell(cellNum);
        boolean shouldHavePhotos = bande.getMission().getCapteur().getFormat()
                .equals(CapteurFormat.MATRICIELLE.getDescription());
        cell.setCellValue(shouldHavePhotos ? String.valueOf(totalBandePhotoPlan) : DEFAULT_EMPTY);
        if (shouldHavePhotos && totalBandePhotoPlan == 0) {
            style = XlsUtils.createForegroundColorStyle(workbook, IndexedColors.RED.getIndex());
            style.setAlignment(HorizontalAlignment.CENTER);
        }
        cell.setCellStyle(style);
    }

    public CellStyle createHeaderBandeTableStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        return style;
    }

    public void setBandeNumCell(Row row, int cellNum, Bande bande, CellStyle style) {
        Cell cell = row.createCell(cellNum);
        cell.setCellValue(bande.getNom().toUpperCase());
        cell.setCellStyle(style);
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
