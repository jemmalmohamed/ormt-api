package ma.org.ormt.modules.indicateurs.indicateur.services.export.impl;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ma.org.ormt.core.commun.base.service.BaseServiceImpl;
import ma.org.ormt.core.commun.base.service.SpecificationService;
import ma.org.ormt.core.validators.ObjectsValidator;
import ma.org.ormt.modules.indicateurs.donnee.models.DonneeIndicateur;
import ma.org.ormt.modules.indicateurs.donnee.repositories.DonneeIndicateurRepository;
import ma.org.ormt.modules.indicateurs.indicateur.dtos.export.IndicateurExportRequest;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.IndicateurExportService;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.formats.csv.IndicateurExportCsvService;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.formats.pdf.IndicateurExportPdfService;
import ma.org.ormt.modules.indicateurs.indicateur.services.export.formats.xls.IndicateurExportExcelService;

@Service
@Transactional
public class IndicateurExportServiceImpl extends BaseServiceImpl<DonneeIndicateur> implements IndicateurExportService {

    @Autowired
    private IndicateurExportExcelService excelService;

    @Autowired
    private IndicateurExportCsvService csvService;

    @Autowired
    private IndicateurExportPdfService pdfService;

    @Autowired
    private ObjectsValidator<IndicateurExportRequest> validator;

    @Autowired
    public IndicateurExportServiceImpl(
            DonneeIndicateurRepository donneeIndicateurRepository,
            SpecificationService specificationService) {
        super(donneeIndicateurRepository, specificationService);
    }

    /**
     * Exporte les données d'un indicateur selon le format et les options de la
     * requête
     */
    public ResponseEntity<byte[]> exportIndicateurDonnees(Indicateur indicateur,
            IndicateurExportRequest request)
            throws IOException {
        validator.validate(request);
        if (indicateur == null) {
            return ResponseEntity.badRequest().body(new byte[0]);
        }

        switch (request.getFormat()) {
            case "xlsx":
                return excelService.export(indicateur, request);
            case "csv":
                return csvService.export(indicateur, request);
            case "pdf":
                return pdfService.export(indicateur, request);
            default:
                return ResponseEntity.badRequest().body(new byte[0]);
        }
    }

}