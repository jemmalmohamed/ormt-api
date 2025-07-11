package ma.org.ormt.modules.indicateurs.indicateur.services.export.single.formats.xls;

import java.io.IOException;

import org.springframework.http.ResponseEntity;

import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.dtos.IndicateurExportRequestDto;

public interface IndicateurSingleExportExcelService {

    /**
     * Enhanced export method with advanced options
     * 
     * @param indicateur    The indicateur to export
     * @param exportRequest Advanced export configuration with section control,
     *                      format options, etc.
     * @return ResponseEntity containing the Excel file bytes
     * @throws IOException if export generation fails
     */
    ResponseEntity<byte[]> exportIndicateurWithOptions(Indicateur indicateur, IndicateurExportRequestDto exportRequest)
            throws IOException;

}
