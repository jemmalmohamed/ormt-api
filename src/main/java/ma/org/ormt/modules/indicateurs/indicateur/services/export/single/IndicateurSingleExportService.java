package ma.org.ormt.modules.indicateurs.indicateur.services.export.single;

import ma.org.ormt.core.commun.base.service.BaseService;
import ma.org.ormt.modules.indicateurs.donnee.models.DonneeIndicateur;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.dtos.IndicateurExportRequestDto;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;

import org.springframework.http.ResponseEntity;

import java.io.IOException;

public interface IndicateurSingleExportService extends BaseService<DonneeIndicateur> {

    /**
     * Enhanced export method using advanced export options
     * Supports section-based export, multiple formats, and flexible configurations
     *
     * @param indicateur    The indicateur to export
     * @param exportRequest Advanced export configuration with section control,
     *                      format options, etc.
     * @return ResponseEntity containing the exported file bytes
     * @throws IOException if export generation fails
     */
    ResponseEntity<byte[]> exportIndicateurWithOptions(Indicateur indicateur, IndicateurExportRequestDto exportRequest)
            throws IOException;

}