package ma.org.ormt.modules.indicateurs.indicateur.services.export;

import ma.org.ormt.core.commun.base.service.BaseService;
import ma.org.ormt.modules.indicateurs.donnee.models.DonneeIndicateur;
import ma.org.ormt.modules.indicateurs.indicateur.dtos.export.IndicateurExportRequest;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;

import org.springframework.http.ResponseEntity;

import java.io.IOException;

public interface IndicateurExportService extends BaseService<DonneeIndicateur> {

    ResponseEntity<byte[]> exportIndicateurDonnees(Indicateur indicateur, IndicateurExportRequest request)
            throws IOException;

}