package ma.org.ormt.modules.indicateurs.donnee.dtos.imports;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DonneeImportCommitDto extends DonneeImportPreviewDto {
    private int importedRows;
    private int overwrittenRows;
    private int skippedConflictRows;
    private int beforeCount;
    private int afterCount;
}
