package ma.org.ormt.core.utilities;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EnhancedPdfExportUtils {

    // Font definitions
    private static final Font TITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLACK);
    private static final Font HEADER_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);
    private static final Font DATA_FONT = FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.BLACK);
    private static final Font SMALL_DATA_FONT = FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.BLACK);

    // Colors
    private static final BaseColor HEADER_COLOR = new BaseColor(64, 64, 64); // Dark gray
    private static final BaseColor ALT_ROW_COLOR = new BaseColor(245, 245, 245); // Light gray

    /**
     * Génère un PDF optimisé avec gestion intelligente de l'orientation et du
     * formatage
     * pour les tableaux pivot complexes.
     */
    @SafeVarargs
    public static byte[] generateOptimizedPdf(String title, List<List<String>>... tables) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            // Determine best orientation based on table complexity
            boolean useLandscape = shouldUseLandscape(tables);
            Rectangle pageSize = useLandscape ? PageSize.A4.rotate() : PageSize.A4;

            Document document = new Document(pageSize, 36, 36, 54, 54); // margins: left, right, top, bottom
            PdfWriter.getInstance(document, out);
            document.open();

            // Add title
            if (title != null && !title.isEmpty()) {
                Paragraph titlePara = new Paragraph(title, TITLE_FONT);
                titlePara.setAlignment(Element.ALIGN_CENTER);
                titlePara.setSpacingAfter(20);
                document.add(titlePara);
            }

            // Process each table
            for (int tableIndex = 0; tableIndex < tables.length; tableIndex++) {
                List<List<String>> tableData = tables[tableIndex];
                if (tableData == null || tableData.isEmpty()) {
                    continue;
                }

                // Add table with optimized formatting
                PdfPTable table = createOptimizedTable(tableData, useLandscape);
                document.add(table);

                // Add spacing between tables
                if (tableIndex < tables.length - 1) {
                    document.add(new Paragraph(" ", DATA_FONT));
                }
            }

            document.close();
            return out.toByteArray();
        } catch (DocumentException e) {
            throw new IOException("Erreur lors de la génération du PDF optimisé", e);
        }
    }

    /**
     * Génère un PDF optimisé pour une seule table
     */
    @SuppressWarnings("unchecked")
    public static byte[] generateOptimizedPdf(String title, List<List<String>> singleTable) throws IOException {
        return generateOptimizedPdf(title, new List[] { singleTable });
    }

    /**
     * Détermine si l'orientation paysage est préférable basée sur la complexité du
     * tableau
     */
    @SafeVarargs
    private static boolean shouldUseLandscape(List<List<String>>... tables) {
        for (List<List<String>> tableData : tables) {
            if (tableData == null || tableData.isEmpty()) {
                continue;
            }

            int maxColumns = tableData.stream()
                    .mapToInt(List::size)
                    .max()
                    .orElse(0);

            // Use landscape if table has more than 6 columns or long content
            if (maxColumns > 6) {
                return true;
            }

            // Check for long content that might benefit from landscape
            for (List<String> row : tableData) {
                for (String cell : row) {
                    if (cell != null && cell.length() > 15) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Overloaded method for single table landscape detection
     */
    private static boolean shouldUseLandscape(List<List<String>> tableData) {
        if (tableData == null || tableData.isEmpty()) {
            return false;
        }

        int maxColumns = tableData.stream()
                .mapToInt(List::size)
                .max()
                .orElse(0);

        // Use landscape if table has more than 6 columns or long content
        if (maxColumns > 6) {
            return true;
        }

        // Check for long content that might benefit from landscape
        for (List<String> row : tableData) {
            for (String cell : row) {
                if (cell != null && cell.length() > 15) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Crée un tableau PDF optimisé avec formatage intelligent
     */
    private static PdfPTable createOptimizedTable(List<List<String>> tableData, boolean isLandscape)
            throws DocumentException {
        if (tableData.isEmpty()) {
            return new PdfPTable(1);
        }

        int numColumns = tableData.get(0).size();
        PdfPTable table = new PdfPTable(numColumns);

        // Set table width to 100% of available space
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.setSpacingAfter(10);

        // Calculate optimal column widths
        float[] columnWidths = calculateColumnWidths(tableData, isLandscape);
        table.setWidths(columnWidths);

        // Detect header rows (typically first few rows with repeated patterns)
        int headerRowCount = detectHeaderRows(tableData);

        // Add table content
        for (int rowIndex = 0; rowIndex < tableData.size(); rowIndex++) {
            List<String> row = tableData.get(rowIndex);
            boolean isHeaderRow = rowIndex < headerRowCount;
            boolean isAlternateRow = !isHeaderRow && (rowIndex - headerRowCount) % 2 == 1;

            for (int colIndex = 0; colIndex < row.size(); colIndex++) {
                String cellValue = row.get(colIndex);
                if (cellValue == null) {
                    cellValue = "";
                }

                PdfPCell cell = createFormattedCell(cellValue, isHeaderRow, isAlternateRow, isLandscape);

                // Special formatting for first column (usually row headers)
                if (colIndex == 0 && !isHeaderRow) {
                    cell.setBackgroundColor(new BaseColor(250, 250, 250));
                    Font boldFont = new Font(DATA_FONT.getBaseFont(), DATA_FONT.getSize(), Font.BOLD);
                    cell.setPhrase(new Phrase(cellValue, boldFont));
                }

                table.addCell(cell);
            }
        }

        return table;
    }

    /**
     * Crée une cellule formatée avec les bonnes propriétés
     */
    private static PdfPCell createFormattedCell(String content, boolean isHeader, boolean isAlternateRow,
            boolean isLandscape) {
        Font cellFont = isHeader ? HEADER_FONT : (isLandscape ? SMALL_DATA_FONT : DATA_FONT);
        PdfPCell cell = new PdfPCell(new Phrase(content, cellFont));

        // Set padding for better readability
        cell.setPadding(isLandscape ? 3 : 5);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);

        // Set background colors
        if (isHeader) {
            cell.setBackgroundColor(HEADER_COLOR);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        } else if (isAlternateRow) {
            cell.setBackgroundColor(ALT_ROW_COLOR);
        }

        // Border styling
        cell.setBorderWidth(0.5f);
        cell.setBorderColor(BaseColor.GRAY);

        // Enable text wrapping for long content
        cell.setNoWrap(false);

        return cell;
    }

    /**
     * Calcule les largeurs optimales des colonnes
     */
    private static float[] calculateColumnWidths(List<List<String>> tableData, boolean isLandscape) {
        if (tableData.isEmpty()) {
            return new float[] { 1 };
        }

        int numColumns = tableData.get(0).size();
        float[] maxWidths = new float[numColumns];

        // Calculate content-based widths
        for (List<String> row : tableData) {
            for (int i = 0; i < Math.min(row.size(), numColumns); i++) {
                String cell = row.get(i);
                if (cell != null) {
                    // Estimate width based on content length
                    float contentWidth = Math.min(cell.length() * 6, isLandscape ? 120 : 80);
                    maxWidths[i] = Math.max(maxWidths[i], contentWidth);
                }
            }
        }

        // Normalize and apply minimum widths
        float totalWidth = 0;
        for (int i = 0; i < numColumns; i++) {
            maxWidths[i] = Math.max(maxWidths[i], isLandscape ? 40 : 30); // minimum width
            totalWidth += maxWidths[i];
        }

        // Normalize to proportional widths
        for (int i = 0; i < numColumns; i++) {
            maxWidths[i] = (maxWidths[i] / totalWidth) * 100;
        }

        // Ensure first column (row headers) gets adequate space
        if (numColumns > 1) {
            maxWidths[0] = Math.max(maxWidths[0], 20);
        }

        return maxWidths;
    }

    /**
     * Détecte le nombre de lignes d'en-tête basé sur les patterns répétitifs
     */
    private static int detectHeaderRows(List<List<String>> tableData) {
        if (tableData.size() < 2) {
            return 1;
        }

        // Look for the pattern where numerical data starts
        for (int i = 1; i < Math.min(tableData.size(), 5); i++) {
            List<String> row = tableData.get(i);
            boolean hasNumericData = false;

            for (int j = 1; j < row.size(); j++) { // Skip first column (labels)
                String cell = row.get(j);
                if (cell != null && !cell.trim().isEmpty()) {
                    try {
                        Double.parseDouble(cell.replace(",", "."));
                        hasNumericData = true;
                        break;
                    } catch (NumberFormatException e) {
                        // Not numeric, continue checking
                    }
                }
            }

            if (hasNumericData) {
                return i; // This is where data starts, so previous rows are headers
            }
        }

        return 1; // Default to 1 header row
    }

    /**
     * Génère un PDF spécifiquement optimisé pour les tableaux pivot
     */
    public static byte[] generatePivotTablePdf(String title, List<List<String>> pivotData) throws IOException {
        if (pivotData == null || pivotData.isEmpty()) {
            @SuppressWarnings("unchecked")
            List<List<String>>[] emptyArray = new List[] { pivotData };
            return generateOptimizedPdf(title, emptyArray);
        }

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            // Always use landscape for pivot tables
            Document document = new Document(PageSize.A4.rotate(), 20, 20, 30, 30);
            PdfWriter.getInstance(document, out);
            document.open();

            // Add title
            if (title != null && !title.isEmpty()) {
                Paragraph titlePara = new Paragraph(title, TITLE_FONT);
                titlePara.setAlignment(Element.ALIGN_CENTER);
                titlePara.setSpacingAfter(15);
                document.add(titlePara);
            }

            // Create pivot table with special formatting
            PdfPTable table = createPivotTable(pivotData);
            document.add(table);

            document.close();
            return out.toByteArray();
        } catch (DocumentException e) {
            throw new IOException("Erreur lors de la génération du PDF pivot", e);
        }
    }

    /**
     * Crée un tableau pivot avec formatage spécialisé
     */
    private static PdfPTable createPivotTable(List<List<String>> pivotData) throws DocumentException {
        int numColumns = pivotData.get(0).size();
        PdfPTable table = new PdfPTable(numColumns);

        table.setWidthPercentage(100);
        table.setSpacingBefore(5);

        // For pivot tables, make all columns roughly equal except the first
        float[] columnWidths = new float[numColumns];
        columnWidths[0] = 20; // Row header column
        float remainingWidth = 80;
        float colWidth = remainingWidth / (numColumns - 1);
        for (int i = 1; i < numColumns; i++) {
            columnWidths[i] = colWidth;
        }
        table.setWidths(columnWidths);

        // Detect header structure for pivot table
        int headerRowCount = detectPivotHeaderRows(pivotData);

        // Add cells with pivot-specific formatting
        for (int rowIndex = 0; rowIndex < pivotData.size(); rowIndex++) {
            List<String> row = pivotData.get(rowIndex);
            boolean isHeaderRow = rowIndex < headerRowCount;

            for (int colIndex = 0; colIndex < row.size(); colIndex++) {
                String cellValue = row.get(colIndex);
                if (cellValue == null) {
                    cellValue = "";
                }

                PdfPCell cell = createPivotCell(cellValue, isHeaderRow, rowIndex, colIndex);
                table.addCell(cell);
            }
        }

        return table;
    }

    /**
     * Détecte les lignes d'en-tête pour les tableaux pivot
     */
    private static int detectPivotHeaderRows(List<List<String>> pivotData) {
        // For pivot tables, typically multiple header rows
        for (int i = 0; i < Math.min(pivotData.size(), 4); i++) {
            List<String> row = pivotData.get(i);
            if (row.size() > 1) {
                // Check if this row contains mostly numeric data (excluding first column)
                int numericCells = 0;
                int totalCells = 0;

                for (int j = 1; j < row.size(); j++) {
                    String cell = row.get(j);
                    if (cell != null && !cell.trim().isEmpty()) {
                        totalCells++;
                        try {
                            Double.parseDouble(cell.replace(",", "."));
                            numericCells++;
                        } catch (NumberFormatException e) {
                            // Not numeric
                        }
                    }
                }

                // If more than 50% of cells are numeric, this is a data row
                if (totalCells > 0 && (double) numericCells / totalCells > 0.5) {
                    return i;
                }
            }
        }

        return Math.min(2, pivotData.size()); // Default to 2 header rows for pivot tables
    }

    /**
     * Crée une cellule spécialement formatée pour les tableaux pivot
     */
    private static PdfPCell createPivotCell(String content, boolean isHeader, int rowIndex, int colIndex) {
        Font cellFont = isHeader ? HEADER_FONT : SMALL_DATA_FONT;
        PdfPCell cell = new PdfPCell(new Phrase(content, cellFont));

        cell.setPadding(3);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        if (isHeader) {
            cell.setBackgroundColor(HEADER_COLOR);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        } else if (colIndex == 0) {
            // Row headers
            cell.setBackgroundColor(new BaseColor(240, 240, 240));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            Font boldFont = new Font(SMALL_DATA_FONT.getBaseFont(), SMALL_DATA_FONT.getSize(), Font.BOLD);
            cell.setPhrase(new Phrase(content, boldFont));
        } else {
            // Data cells
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            if (rowIndex % 2 == 1) {
                cell.setBackgroundColor(ALT_ROW_COLOR);
            }
        }

        cell.setBorderWidth(0.3f);
        cell.setBorderColor(BaseColor.GRAY);
        cell.setNoWrap(false);

        return cell;
    }

    /**
     * Prépare la réponse HTTP pour le téléchargement du PDF optimisé.
     */
    public static ResponseEntity<byte[]> exportOptimizedPdfFormat(byte[] pdfBytes, String fileName) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", fileName);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    /**
     * Génère un PDF optimisé contenant plusieurs tables avec une mise en forme
     * intelligente.
     */
    public static byte[] generateMultiTablePdf(String title, List<List<String>>[] tables, String fileName) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);

            document.open();

            // Add title
            if (title != null && !title.trim().isEmpty()) {
                Paragraph titleParagraph = new Paragraph(title, TITLE_FONT);
                titleParagraph.setAlignment(Element.ALIGN_CENTER);
                titleParagraph.setSpacingAfter(20);
                document.add(titleParagraph);
            }

            // Process each table
            for (int tableIndex = 0; tableIndex < tables.length; tableIndex++) {
                List<List<String>> tableData = tables[tableIndex];

                if (tableData == null || tableData.isEmpty()) {
                    continue;
                }

                // Add table section title
                String sectionTitle = getSectionTitle(tableIndex, tableData);
                if (sectionTitle != null) {
                    Paragraph sectionParagraph = new Paragraph(sectionTitle, HEADER_FONT);
                    sectionParagraph.setSpacingBefore(tableIndex > 0 ? 20 : 0);
                    sectionParagraph.setSpacingAfter(10);
                    document.add(sectionParagraph);
                }

                // Determine if this table should use landscape orientation
                boolean shouldUseLandscape = shouldUseLandscape(tableData);

                // If landscape is needed and we're in portrait, add a new page
                if (shouldUseLandscape && document.getPageSize().equals(PageSize.A4)) {
                    document.newPage();
                    document.setPageSize(PageSize.A4.rotate());
                    document.newPage();
                } else if (!shouldUseLandscape && document.getPageSize().equals(PageSize.A4.rotate())) {
                    document.newPage();
                    document.setPageSize(PageSize.A4);
                    document.newPage();
                }

                // Create and add the table
                PdfPTable pdfTable = createOptimizedTable(tableData, shouldUseLandscape);
                document.add(pdfTable);

                // Add spacing between tables
                if (tableIndex < tables.length - 1) {
                    document.add(new Paragraph("\n"));
                }
            }

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du PDF multi-tables : " + e.getMessage(), e);
        }
    }

    /**
     * Détermine le titre de section basé sur l'index et le contenu de la table
     */
    private static String getSectionTitle(int tableIndex, List<List<String>> tableData) {
        // Try to infer section type from table content
        if (tableData.size() > 0 && tableData.get(0).size() > 0) {
            String firstCell = tableData.get(0).get(0);
            if (firstCell != null) {
                // Check for metadata patterns
                if (firstCell.toLowerCase().contains("meta") ||
                        firstCell.toLowerCase().contains("information") ||
                        firstCell.toLowerCase().contains("propriété")) {
                    return "Métadonnées";
                }
                // Check for pivot patterns
                if (tableData.size() > 2 && tableData.get(0).size() > 3) {
                    return "Tableau croisé dynamique";
                }
                // Default to flat data
                if (tableIndex == 0)
                    return "Données";
            }
        }

        return "Section " + (tableIndex + 1);
    }
}
