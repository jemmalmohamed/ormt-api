package ma.org.ormt.modules.indicateurs.indicateur.services.export.formats.csv;

import ma.org.ormt.modules.indicateurs.indicateur.dtos.export.IndicateurExportRequest;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;
import org.springframework.http.ResponseEntity;
import java.io.IOException;

public interface IndicateurExportCsvService {
    ResponseEntity<byte[]> export(Indicateur indicateur, IndicateurExportRequest request) throws IOException;
}
