package ma.org.ormt.modules.indicateur.services;

import java.io.InputStream;
import ma.org.ormt.modules.indicateur.controllers.IndicateurImportController.ImportResult;

public interface IndicateurImportService {
    ImportResult importFromExcel(InputStream inputStream, String sheetName);
}
