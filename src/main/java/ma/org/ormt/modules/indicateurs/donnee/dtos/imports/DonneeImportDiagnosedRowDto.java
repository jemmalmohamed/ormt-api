package ma.org.ormt.modules.indicateurs.donnee.dtos.imports;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DonneeImportDiagnosedRowDto {
    private int rowNumber;
    private String status;
    private String reason;
    private String dimensionsLabel;
    private String valeur;
    private Long existingDonneeId;
    private String existingValeur;
    private String importedValeur;
}
