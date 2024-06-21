package ma.org.ancfcc.pva.core.utilities;

import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class XlsUtils {

    private static final String CELL_IS_NULL_OR_BLANK = "cell is null or blank.";

    public static Integer getIntegerFromCell(Cell cell) throws IOException {
        if (cell != null) {
            switch (cell.getCellType()) {
                case NUMERIC:
                    // Convert double to int safely assuming no overflow
                    return (int) cell.getNumericCellValue();
                case STRING:
                    try {
                        // Parse the string to double first and then convert to int
                        return Integer.parseInt(cell.getStringCellValue().trim());
                    } catch (NumberFormatException e) {
                        // Throw a more specific exception if string cannot be parsed to int
                        throw new IOException("Failed to parse 'Entier'  from string value: "
                                + cell.getStringCellValue(), e);
                    }
                default:
                    throw new IOException("Unsupported cell type for 'Code Carte': " + cell.getCellType());
            }
        } else {
            throw new IOException(CELL_IS_NULL_OR_BLANK);
        }
    }

    public static Long getLongFromCell(Cell cell) throws IOException {
        if (cell != null) {
            switch (cell.getCellType()) {
                case NUMERIC:
                    // Convert double to int safely assuming no overflow
                    return (long) cell.getNumericCellValue();
                case STRING:
                    try {
                        // Parse the string to double first and then convert to int
                        return Long.parseLong(cell.getStringCellValue().trim());
                    } catch (NumberFormatException e) {
                        // Throw a more specific exception if string cannot be parsed to int
                        throw new IOException("Failed to parse 'Long'  from string value: "
                                + cell.getStringCellValue(), e);
                    }
                default:
                    throw new IOException("Unsupported cell type for 'Code Carte': " + cell.getCellType());
            }
        } else {
            throw new IOException(CELL_IS_NULL_OR_BLANK);
        }
    }

    public static String getStringFromCell(Cell cell) throws IOException {
        if (cell != null) {
            switch (cell.getCellType()) {
                case NUMERIC:
                    double numValue = cell.getNumericCellValue();
                    if (numValue % 1 == 0) { // Check if the number is an integer
                        // If it's an integer, cast to int and convert to String
                        return String.valueOf((int) numValue);
                    } else {
                        // If not an integer, handle as floating point value
                        return String.valueOf(numValue);
                    }
                case STRING:

                    return cell.getStringCellValue();

                default:
                    throw new IOException("Unsupported cell type for : " + cell.getCellType());
            }
        } else {
            throw new IOException(CELL_IS_NULL_OR_BLANK);
        }
    }

}
