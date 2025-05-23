package ma.org.ormt.core.utilities;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CsvUtils {
    /**
     * Convertit une ligne en CSV, en gérant l'échappement et le formatage des
     * nombres en notation scientifique.
     */
    public static String rowToCsv(List<String> row) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < row.size(); i++) {
            String value = row.get(i);
            if (value != null) {
                // Formater les nombres en notation scientifique
                try {
                    if (value.matches("-?\\d+\\.\\d+E[+-]?\\d+")) {
                        double d = Double.parseDouble(value);
                        value = String.format("%.2f", d);
                    }
                } catch (NumberFormatException e) {
                    // Laisser tel quel
                }
                value = value.replace("\"", "\"\""); // doubler les guillemets
            } else {
                value = "";
            }
            sb.append('"').append(value).append('"');
            if (i < row.size() - 1)
                sb.append(";");
        }
        return sb.toString();
    }

    /**
     * Ajoute le BOM UTF-8 au début du tableau d'octets CSV.
     */
    public static byte[] addUtf8Bom(byte[] csvBytes) {
        byte[] bom = new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF };
        byte[] output = new byte[bom.length + csvBytes.length];
        System.arraycopy(bom, 0, output, 0, bom.length);
        System.arraycopy(csvBytes, 0, output, bom.length, csvBytes.length);
        return output;
    }

    /**
     * Prépare la réponse HTTP pour le téléchargement du fichier CSV.
     */
    public static ResponseEntity<byte[]> exportCsvFormat(byte[] csvBytes, String fileName) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));
        headers.setContentDispositionFormData("attachment", fileName);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return new ResponseEntity<>(csvBytes, headers, HttpStatus.OK);
    }
}
