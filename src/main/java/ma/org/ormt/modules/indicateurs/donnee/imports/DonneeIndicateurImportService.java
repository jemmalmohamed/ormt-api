package ma.org.ormt.modules.indicateurs.donnee.imports;

import java.io.InputStream;

import ma.org.ormt.modules.indicateurs.indicateur.helpers.ImportXlsResult;

public interface DonneeIndicateurImportService {

    ImportXlsResult parseIndicateurDonneeFromExcel(InputStream inputStream, String sheetName);
}
