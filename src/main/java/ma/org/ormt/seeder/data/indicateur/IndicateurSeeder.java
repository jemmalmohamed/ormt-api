package ma.org.ormt.seeder.data.indicateur;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ma.org.ormt.modules.domaines.sousdomaine.models.SousDomaine;
import ma.org.ormt.modules.indicateurs.dimension.dtos.DomaineCreateRequestDto.IndicateurCreateRequestDto;
import ma.org.ormt.modules.indicateurs.dimension.dtos.DomaineCreateRequestDto.IndicateurCreateRequestDto.DimensionCreateRequestDto;
import ma.org.ormt.modules.indicateurs.dimension.models.Dimension;
import ma.org.ormt.modules.indicateurs.dimension.services.DimensionService;
import ma.org.ormt.modules.indicateurs.indicateur.association.repository.IndicateurDimensionRepository;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;
import ma.org.ormt.modules.indicateurs.indicateur.models.IndicateurDimension;
import ma.org.ormt.modules.indicateurs.indicateur.services.indicateur.IndicateurService;
import ma.org.ormt.modules.indicateurs.source.models.Source;
import ma.org.ormt.modules.indicateurs.source.services.SourceService;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Log4j2
@Component
@RequiredArgsConstructor
public class IndicateurSeeder {
    private final IndicateurService indicateurService;
    private final DimensionService dimensionService;
    private final SourceService sourceService;
    private final IndicateurDimensionRepository indicateurDimensionRepository;

    public void processIndicateurs(List<IndicateurCreateRequestDto> indicateurs, SousDomaine parentSousDomaine) {
        if (indicateurs == null || indicateurs.isEmpty()) {
            return;
        }
        indicateurs.forEach(indicateurRequest -> {
            try {
                createIndicateur(indicateurRequest, parentSousDomaine);
            } catch (Exception e) {
                log.error("Error creating indicateur {}: {}", indicateurRequest.getNom(), e.getMessage());
            }
        });
    }

    @Transactional
    public void createIndicateur(IndicateurCreateRequestDto indicateurRequest, SousDomaine parentSousDomaine) {
        try {
            Indicateur newIndicateur = new Indicateur();
            newIndicateur.setNom(indicateurRequest.getNom().toLowerCase());
            newIndicateur.setCategorie(indicateurRequest.getCategorie().toLowerCase());
            newIndicateur.setActif(indicateurRequest.getActif());
            newIndicateur.setAbreviation(indicateurRequest.getAbreviation().toLowerCase());
            newIndicateur.setTypeTb(indicateurRequest.getTypeTb());
            newIndicateur.setRegleCalcul(indicateurRequest.getRegleCalcul());
            newIndicateur.setUnite(indicateurRequest.getUnite());
            newIndicateur.setDescription(indicateurRequest.getDescription().toLowerCase());
            newIndicateur.getSousDomaines().add(parentSousDomaine);

            Indicateur savedIndicateur = indicateurService.create(newIndicateur);
            handleSourceIndicateur(indicateurRequest.getSource(), savedIndicateur);
            if (indicateurRequest.getDimensions() != null) {
                for (DimensionCreateRequestDto dimensionRequest : indicateurRequest.getDimensions()) {
                    try {
                        handleDimensionInNewTransaction(dimensionRequest, savedIndicateur.getId());
                    } catch (Exception e) {
                        log.error("Failed to add dimension {} to indicateur {}: {}",
                                dimensionRequest.getNom(), savedIndicateur.getNom(), e.getMessage());
                    }
                }
            }
            log.info("Created indicateur: {}", savedIndicateur.getNom());
        } catch (Exception e) {
            log.error("Error in createIndicateur: {}", e.getMessage());
            throw new RuntimeException("Failed to create indicateur", e);
        }
    }

    @Transactional
    public void handleDimensionInNewTransaction(DimensionCreateRequestDto dimensionRequest, Long indicateurId) {
        try {
            Indicateur indicateur = indicateurService.findById(indicateurId)
                    .orElseThrow(() -> new RuntimeException("Indicateur not found"));
            Dimension dimension = dimensionService.findByNom(dimensionRequest.getNom())
                    .orElseGet(() -> {
                        try {
                            Dimension newDimension = new Dimension();
                            newDimension.setNom(dimensionRequest.getNom().toLowerCase());
                            newDimension.setType(dimensionRequest.getType().toLowerCase());
                            newDimension.setDescription("");
                            newDimension.setLibelle(dimensionRequest.getLibelle().toLowerCase());
                            return dimensionService.save(newDimension);
                        } catch (DataIntegrityViolationException | ConstraintViolationException ex) {
                            // Another thread/process created the dimension at the same time
                            log.warn(
                                    "Dimension '{}' already exists (created concurrently, hibernate). Fetching existing.",
                                    dimensionRequest.getNom());
                            return dimensionService.findByNom(dimensionRequest.getNom())
                                    .orElseThrow(() -> new RuntimeException(
                                            "Dimension not found after duplicate key: " + dimensionRequest.getNom()));
                        } catch (Exception ex) {
                            // Defensive: check if cause is constraint violation
                            Throwable cause = ex.getCause();
                            if (cause instanceof ConstraintViolationException) {
                                log.warn(
                                        "Dimension '{}' already exists (created concurrently, cause). Fetching existing.",
                                        dimensionRequest.getNom());
                                return dimensionService.findByNom(dimensionRequest.getNom())
                                        .orElseThrow(
                                                () -> new RuntimeException("Dimension not found after duplicate key: "
                                                        + dimensionRequest.getNom()));
                            }
                            throw ex;
                        }
                    });
            indicateurService.save(indicateur);
            boolean isPrincipale = dimensionRequest.getAssociation().getPrincipale();
            boolean isTemporelle = dimensionRequest.getAssociation().getTemporelle();
            IndicateurDimension indicateurDimension = new IndicateurDimension();
            indicateurDimension.setIndicateur(indicateur);
            indicateurDimension.setDimension(dimension);
            indicateurDimension.setPrincipale(isPrincipale);
            indicateurDimension.setTemporelle(isTemporelle);
            indicateurDimensionRepository.save(indicateurDimension);
        } catch (Exception e) {
            log.error("Error handling dimension {}: {}", dimensionRequest.getNom(), e.getMessage());
            throw e;
        }
    }

    @Transactional
    public void handleSourceIndicateur(String source, Indicateur indicateur) {
        try {
            String sourceKey = source.toLowerCase();
            Source sourceEntity;
            try {
                sourceEntity = sourceService.findByNom(sourceKey)
                        .orElseGet(() -> {
                            Source newSource = new Source();
                            newSource.setNom(sourceKey);
                            newSource.setDescription("");
                            return sourceService.save(newSource);
                        });
            } catch (DataIntegrityViolationException | ConstraintViolationException ex) {
                // Another thread/process created the source at the same time
                log.warn("Source '{}' already exists (created concurrently). Fetching existing.", sourceKey);
                sourceEntity = sourceService.findByNom(sourceKey)
                        .orElseThrow(() -> new RuntimeException("Source not found after duplicate key: " + sourceKey));
            } catch (Exception ex) {
                // Defensive: check if cause is constraint violation
                Throwable cause = ex.getCause();
                if (cause instanceof ConstraintViolationException) {
                    log.warn("Source '{}' already exists (created concurrently, cause). Fetching existing.", sourceKey);
                    sourceEntity = sourceService.findByNom(sourceKey)
                            .orElseThrow(
                                    () -> new RuntimeException("Source not found after duplicate key: " + sourceKey));
                } else {
                    throw ex;
                }
            }
            indicateur.setSource(sourceEntity);
            indicateurService.save(indicateur);
        } catch (Exception e) {
            log.error("Error handling source for indicateur {}: {}", indicateur.getNom(), e.getMessage());
            throw e;
        }
    }

}
