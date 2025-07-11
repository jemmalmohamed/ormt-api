package ma.org.ormt.core.utilities;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Utility class for Excel (XLS/XLSX) operations using Apache POI.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExcelUtils {
    private static final String CELL_IS_NULL_OR_BLANK = "cell is null or blank.";

    /**
     * Creates a cell with a string value and applies a style if provided.
     */
    public static void createCell(Row row, int colIdx, String value, CellStyle style) {
        Cell cell = row.createCell(colIdx);
        cell.setCellValue(value);
        if (style != null)
            cell.setCellStyle(style);
    }

    /**
     * Creates a cell with a string value (no style).
     */
    public static void createCell(Row row, int colIdx, String value) {
        Cell cell = row.createCell(colIdx);
        cell.setCellValue(value);
    }

    public static void createCell(Row row, int colIdx, Long value) {
        Cell cell = row.createCell(colIdx);
        cell.setCellValue(value);
    }

    /**
     * Creates a cell with auto-type detection (numeric if possible, otherwise
     * string) and applies a style if provided.
     */
    public static void createCellAutoType(Row row, int colIdx, String value, CellStyle style) {
        Cell cell = row.createCell(colIdx);
        setCellValueAutoType(cell, value);
        if (style != null)
            cell.setCellStyle(style);
    }

    /**
     * Creates a bordered cell style with the given border style.
     */
    public static CellStyle createBorderedCellStyle(Workbook workbook, BorderStyle borderStyle) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderTop(borderStyle);
        style.setBorderBottom(borderStyle);
        style.setBorderLeft(borderStyle);
        style.setBorderRight(borderStyle);
        style.setAlignment(HorizontalAlignment.LEFT);
        return style;
    }

    /**
     * Creates a simple thin-bordered cell style.
     */
    public static CellStyle createBorderedStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    /**
     * Creates a header cell style (bold font, thin border).
     */
    public static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = createBorderedStyle(workbook);
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    /**
     * Creates a cell style with a solid foreground color.
     */
    public static CellStyle createForegroundColorStyle(Workbook workbook, short colorIndex) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(colorIndex);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    /**
     * Auto-sizes columns in the given sheet up to the specified count.
     */
    public static void autoSizeColumns(Sheet sheet, int numColumns) {
        for (int i = 0; i < numColumns; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    /**
     * Auto-sizes all columns in the given sheet based on the widest cell in each
     * column.
     */
    public static void autoSizeColumns(Sheet sheet) {
        int maxCol = 0;
        for (int rowIdx = sheet.getFirstRowNum(); rowIdx <= sheet.getLastRowNum(); rowIdx++) {
            Row row = sheet.getRow(rowIdx);
            if (row != null && row.getLastCellNum() > maxCol) {
                maxCol = row.getLastCellNum();
            }
        }
        for (int colIdx = 0; colIdx < maxCol; colIdx++) {
            sheet.autoSizeColumn(colIdx);
        }
    }

    /**
     * Extracts an Integer from a cell, handling numeric and string types.
     */
    public static Integer getIntegerFromCell(Cell cell) throws IOException {
        if (cell != null) {
            switch (cell.getCellType()) {
                case NUMERIC:
                    return (int) cell.getNumericCellValue();
                case STRING:
                    try {
                        return Integer.parseInt(cell.getStringCellValue().trim());
                    } catch (NumberFormatException e) {
                        throw new IOException("Failed to parse Integer from string value: " + cell.getStringCellValue(),
                                e);
                    }
                default:
                    throw new IOException("Unsupported cell type for Integer: " + cell.getCellType());
            }
        } else {
            throw new IOException(CELL_IS_NULL_OR_BLANK);
        }
    }

    /**
     * Extracts a Long from a cell, handling numeric and string types.
     */
    public static Long getLongFromCell(Cell cell) throws IOException {
        if (cell != null) {
            switch (cell.getCellType()) {
                case NUMERIC:
                    return (long) cell.getNumericCellValue();
                case STRING:
                    try {
                        return Long.parseLong(cell.getStringCellValue().trim());
                    } catch (NumberFormatException e) {
                        throw new IOException("Failed to parse Long from string value: " + cell.getStringCellValue(),
                                e);
                    }
                default:
                    throw new IOException("Unsupported cell type for Long: " + cell.getCellType());
            }
        } else {
            throw new IOException(CELL_IS_NULL_OR_BLANK);
        }
    }

    /**
     * Extracts a String from a cell, handling numeric and string types.
     */
    public static String getStringFromCell(Cell cell) throws IOException {
        if (cell != null) {
            switch (cell.getCellType()) {
                case NUMERIC:
                    double numValue = cell.getNumericCellValue();
                    if (numValue % 1 == 0) {
                        return String.valueOf((int) numValue);
                    } else {
                        return String.valueOf(numValue);
                    }
                case STRING:
                    return cell.getStringCellValue();
                default:
                    throw new IOException("Unsupported cell type for String: " + cell.getCellType());
            }
        } else {
            throw new IOException(CELL_IS_NULL_OR_BLANK);
        }
    }

    /**
     * Prepares a ResponseEntity for downloading an Excel file.
     */
    public static ResponseEntity<byte[]> exportExcelFormat(ByteArrayOutputStream outputStream, String fileName) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.ms-excel"));
        headers.setContentDispositionFormData("attachment", "export_" + fileName + ".xlsx");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
    }

    /**
     * Sets the cell value as a number if possible, otherwise as a string.
     */
    public static void setCellValueAutoType(Cell cell, String value) {
        if (value == null) {
            cell.setBlank();
            return;
        }
        try {
            double numericValue = Double.parseDouble(value);
            cell.setCellValue(numericValue);
        } catch (NumberFormatException e) {
            cell.setCellValue(value);
        }
    }

}
