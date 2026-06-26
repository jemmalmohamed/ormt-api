package ma.org.ormt.modules.indicateurs.donnee.dtos.imports;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DonneeImportConflictDto extends DonneeImportRowIssueDto {
    private Long existingDonneeId;
    private String existingValeur;
    private String importedValeur;
}
