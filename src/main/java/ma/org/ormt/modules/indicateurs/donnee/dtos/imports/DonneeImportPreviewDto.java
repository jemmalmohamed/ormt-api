package ma.org.ormt.modules.indicateurs.donnee.dtos.imports;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DonneeImportPreviewDto {
    private int rowsReceived;
    private int validRows;
    private int dimensionsMapped;
    private int newRows;
    private int duplicateRows;
    private int conflictRows;
    private int rejectedRows;
    private int missingValueRows;
    private int missingDimensionsRows;
    private Double minValue;
    private Double maxValue;
    private boolean hasConflicts;
    private boolean hasBlockingErrors;
    private List<DonneeImportDiagnosedRowDto> diagnosticRows = new ArrayList<>();
    private List<DonneeImportConflictDto> conflicts = new ArrayList<>();
    private List<DonneeImportRowIssueDto> duplicates = new ArrayList<>();
    private List<DonneeImportRowIssueDto> rejected = new ArrayList<>();
}
