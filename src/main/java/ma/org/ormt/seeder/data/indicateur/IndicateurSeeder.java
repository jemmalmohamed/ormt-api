package ma.org.ormt.seeder.data.indicateur;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ma.org.ormt.modules.domaines.sousdomaine.models.SousDomaine;
import ma.org.ormt.modules.indicateurs.dimension.dtos.DomaineCreateRequestDto.IndicateurCreateRequestDto;
import ma.org.ormt.modules.indicateurs.dimension.dtos.DomaineCreateRequestDto.IndicateurCreateRequestDto.DimensionCreateRequestDto;
import ma.org.ormt.modules.indicateurs.dimension.models.Dimension;
import ma.org.ormt.modules.indicateurs.dimension.services.DimensionService;
import ma.org.ormt.modules.indicateurs.indicateur.association.dimension.models.IndicateurDimension;
import ma.org.ormt.modules.indicateurs.indicateur.association.dimension.repository.IndicateurDimensionRepository;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;
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
            if (indicateurRequest.getSource() != "") {
                handleSourceIndicateur(indicateurRequest.getSource(), savedIndicateur);
            }
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
        if (source == null || source.trim().isEmpty()) {
            log.warn("Source is null or empty for indicateur {}", indicateur.getNom());
            return;
        }

        String sourceKey = source.toLowerCase().trim();
        Source sourceEntity = null;

        // First, try to find existing source
        try {
            sourceEntity = sourceService.findByAbreviation(sourceKey).orElse(null);
        } catch (Exception e) {
            log.warn("Error finding source '{}': {}", sourceKey, e.getMessage());
        }

        // If source doesn't exist, try to create it
        if (sourceEntity == null) {
            try {
                Source newSource = new Source();
                newSource.setNom(sourceKey);
                newSource.setAbreviation(sourceKey);
                newSource.setDescription("");
                sourceEntity = sourceService.save(newSource);
                log.info("Created new source: {}", sourceKey);
            } catch (DataIntegrityViolationException | ConstraintViolationException ex) {
                // Source was created concurrently, fetch it
                log.info("Source '{}' was created concurrently, fetching existing one", sourceKey);
                try {
                    sourceEntity = sourceService.findByAbreviation(sourceKey).orElse(null);
                } catch (Exception e) {
                    log.warn("Failed to fetch existing source '{}' after concurrent creation: {}", sourceKey,
                            e.getMessage());
                }
            } catch (Exception ex) {
                log.warn("Failed to create source '{}': {}", sourceKey, ex.getMessage());
            }
        }

        // Associate source with indicateur if we have a valid source
        if (sourceEntity != null) {
            try {
                indicateur.setSource(sourceEntity);
                indicateurService.save(indicateur);
                log.debug("Successfully associated source '{}' with indicateur '{}'", sourceKey, indicateur.getNom());
            } catch (Exception e) {
                log.warn("Failed to associate source '{}' with indicateur '{}': {}", sourceKey, indicateur.getNom(),
                        e.getMessage());
            }
        } else {
            log.warn("Could not find or create source '{}' for indicateur '{}'", sourceKey, indicateur.getNom());
        }
    }

}
