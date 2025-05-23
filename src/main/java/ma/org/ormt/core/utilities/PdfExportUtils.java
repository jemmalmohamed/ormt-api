package ma.org.ormt.core.utilities;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PdfExportUtils {
    /**
     * Génère un PDF simple avec un titre et des tableaux de données.
     * Chaque table est une List<List<String>>.
     */
    public static byte[] generatePdf(String title, List<List<String>>... tables) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();
            if (title != null && !title.isEmpty()) {
                Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
                Paragraph titlePara = new Paragraph(title, titleFont);
                titlePara.setAlignment(Element.ALIGN_CENTER);
                document.add(titlePara);
                document.add(new Paragraph(" "));
            }
            for (List<List<String>> tableData : tables) {
                if (tableData == null || tableData.isEmpty())
                    continue;
                PdfPTable table = new PdfPTable(tableData.get(0).size());
                table.setWidthPercentage(100);
                for (List<String> row : tableData) {
                    for (String cellValue : row) {
                        PdfPCell cell = new PdfPCell(new Phrase(cellValue != null ? cellValue : ""));
                        table.addCell(cell);
                    }
                }
                document.add(table);
                document.add(new Paragraph(" "));
            }
            document.close();
            return out.toByteArray();
        } catch (DocumentException e) {
            throw new IOException("Erreur lors de la génération du PDF", e);
        }
    }

    /**
     * Prépare la réponse HTTP pour le téléchargement du PDF.
     */
    public static ResponseEntity<byte[]> exportPdfFormat(byte[] pdfBytes, String fileName) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", fileName);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}
