package ma.org.ormt.modules.indicateurs.indicateur.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImportXlsResult {
    @Schema(description = "Number of records successfully imported")
    private int successCount;

    @Schema(description = "Number of records failed to import")
    private int failureCount;

    @Schema(description = "List of error messages for failed imports")
    private List<String> errors = new ArrayList<>();

    @Schema(description = "Extracted row data list")
    private List<ImportedRow> rowDataList = new ArrayList<>();

    // New nested class containing row number + cells
    public static class ImportedRow {
        private int rowIndex;
        private Map<Integer, String> cells;

        public int getRowIndex() {
            return rowIndex;
        }

        public void setRowIndex(int rowIndex) {
            this.rowIndex = rowIndex;
        }

        public Map<Integer, String> getCells() {
            return cells;
        }

        public void setCells(Map<Integer, String> cells) {
            this.cells = cells;
        }
    }
}
