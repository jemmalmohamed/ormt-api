package ma.org.ormt.modules.indicateurs.indicateur.services.export.single.impl;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ma.org.ormt.core.commun.base.service.BaseServiceImpl;
import ma.org.ormt.core.commun.base.service.SpecificationService;
import ma.org.ormt.modules.indicateurs.donnee.models.DonneeIndicateur;
import ma.org.ormt.modules.indicateurs.donnee.repositories.DonneeIndicateurRepository;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.dtos.IndicateurExportRequestDto;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.single.IndicateurSingleExportService;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.single.formats.xls.IndicateurSingleExportExcelService;

@Service
@Transactional
public class IndicateurExportServiceImpl extends BaseServiceImpl<DonneeIndicateur>
        implements IndicateurSingleExportService {

    @Autowired
    private IndicateurSingleExportExcelService excelService;

    @Autowired
    public IndicateurExportServiceImpl(
            DonneeIndicateurRepository donneeIndicateurRepository,
            SpecificationService specificationService) {
        super(donneeIndicateurRepository, specificationService);
    }

    /**
     * Enhanced export method using advanced export options
     */
    @Override
    public ResponseEntity<byte[]> exportIndicateurWithOptions(Indicateur indicateur,
            IndicateurExportRequestDto exportRequest) throws IOException {

        if (indicateur == null) {
            return ResponseEntity.badRequest().body(new byte[0]);
        }

        // Validate export request
        if (exportRequest.getFormat() == null) {
            exportRequest.setFormat(IndicateurExportRequestDto.ExportFormat.EXCEL);
        }

        switch (exportRequest.getFormat()) {
            case EXCEL:
                return excelService.exportIndicateurWithOptions(indicateur, exportRequest);
            case CSV:
                throw new UnsupportedOperationException("CSV export not yet implemented for single indicateur");
            default:
                return ResponseEntity.badRequest().body(new byte[0]);
        }
    }

}