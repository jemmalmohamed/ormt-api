package ma.org.ormt.modules.indicateurs.donnee.dtos.imports;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import ma.org.ormt.modules.indicateurs.donnee.dtos.request.DonneeIndicateurRequestDto;

@Getter
@Setter
public class DonneeImportCommitRequest {
    private List<DonneeIndicateurRequestDto> rows = new ArrayList<>();
    private boolean overwriteConflicts;
    private boolean replaceExistingData;
    private List<Integer> selectedRowNumbers = new ArrayList<>();
}
