package ma.org.ormt.modules.indicateurs.indicateur.services;

import java.io.InputStream;

import ma.org.ormt.modules.indicateurs.indicateur.helpers.ImportXlsResult;

public interface IndicateurImportService {
    ImportXlsResult importIndicateurFromExcel(InputStream inputStream, String sheetName);
}
