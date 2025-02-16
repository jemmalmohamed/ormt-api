package ma.org.ormt.modules.indicateur.services.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ma.org.ormt.modules.indicateur.controllers.IndicateurImportController.ImportResult;
import ma.org.ormt.modules.indicateur.services.IndicateurImportService;
import ma.org.ormt.modules.indicateur.services.IndicateurService;

@Service
@RequiredArgsConstructor
@Log4j2
public class IndicateurImportServiceImpl implements IndicateurImportService {

    private final IndicateurService indicateurService;

    @Override
    public ImportResult importFromExcel(InputStream inputStream, String sheetName) {
        List<String> errors = new ArrayList<>();
        List<Map<Integer, String>> rowDataList = new ArrayList<>();
        int successCount = 0;
        int failureCount = 0;

        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = (sheetName != null && !sheetName.isEmpty())
                    ? workbook.getSheet(sheetName)
                    : workbook.getSheetAt(0);

            if (sheet == null) {
                throw new IllegalArgumentException("Sheet not found");
            }

            for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                try {
                    rowDataList.add(extractRowData(row));
                    successCount++;
                } catch (Exception e) {
                    failureCount++;
                    String errorMessage = String.format("Row %d: %s", i + 1, e.getMessage());
                    errors.add(errorMessage);
                    log.error(errorMessage, e);
                }
            }

        } catch (Exception e) {
            log.error("Error processing Excel file", e);
            errors.add("Global error: " + e.getMessage());
        }

        ImportResult result = new ImportResult();
        result.setSuccessCount(successCount);
        result.setFailureCount(failureCount);
        result.setErrors(errors);
        result.setRowDataList(rowDataList);
        return result;
    }

    private Map<Integer, String> extractRowData(Row row) {
        Map<Integer, String> rowData = new HashMap<>();
        for (int c = 0; c < row.getLastCellNum(); c++) {
            rowData.put(c, getCellValue(row.getCell(c)));
        }
        return rowData;
    }

    private String getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        try {
            switch (cell.getCellType()) {
                case STRING:
                    return cell.getStringCellValue();
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        return cell.getDateCellValue().toString();
                    }
                    return String.valueOf(cell.getNumericCellValue());
                case BOOLEAN:
                    return String.valueOf(cell.getBooleanCellValue());
                case FORMULA:
                    return cell.getCellFormula();
                default:
                    return null;
            }
        } catch (Exception e) {
            log.warn("Error reading cell value", e);
            return null;
        }
    }
}