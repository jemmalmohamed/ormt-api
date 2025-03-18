package ma.org.ormt.seeder.data.domaine;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ma.org.ormt.modules.domaines.domaine.dtos.request.DomaineRequestDto;
import ma.org.ormt.modules.domaines.domaine.models.Domaine;
import ma.org.ormt.modules.domaines.domaine.services.DomaineService;
import ma.org.ormt.modules.domaines.sousdomaine.dtos.request.SousDomaineRequestDto;
import ma.org.ormt.modules.domaines.sousdomaine.models.SousDomaine;
import ma.org.ormt.modules.domaines.sousdomaine.services.SousDomaineService;
import ma.org.ormt.modules.indicateurs.dimension.dtos.DomaineCreateRequestDto;
import ma.org.ormt.modules.indicateurs.dimension.dtos.DomaineCreateRequestDto.IndicateurCreateRequestDto;
import ma.org.ormt.modules.indicateurs.dimension.dtos.DomaineCreateRequestDto.IndicateurCreateRequestDto.DimensionCreateRequestDto;
import ma.org.ormt.modules.indicateurs.dimension.dtos.DomaineCreateRequestDto.SousDomaineCreateRequestDto;
import ma.org.ormt.modules.indicateurs.dimension.models.Dimension;
import ma.org.ormt.modules.indicateurs.dimension.services.DimensionService;
import ma.org.ormt.modules.indicateurs.indicateur.association.repository.IndicateurDimensionRepository;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;
import ma.org.ormt.modules.indicateurs.indicateur.models.IndicateurDimension;
import ma.org.ormt.modules.indicateurs.indicateur.services.IndicateurService;

@Log4j2
@Component
@Order(3)
@RequiredArgsConstructor
public class DomaineSeeder implements CommandLineRunner {

    @Value("${starter.database.seed}")
    private boolean seeding;

    private final DomaineService domaineService;
    private final SousDomaineService sousDomaineService;
    private final IndicateurService indicateurService;
    private final DimensionService dimensionService;
    private final ObjectMapper objectMapper;
    private final IndicateurDimensionRepository indicateurDimensionRepository;

    @Override
    public void run(String... args) {
        if (!seeding) {
            log.info("Seeding is disabled. Skipping domain data seeding.");
            return;
        }

        try {
            Path resourcePath = Paths.get("src/main/resources/init-data/domaines");
            if (!Files.exists(resourcePath)) {
                log.warn("Resource path {} does not exist. Skipping domain data seeding.", resourcePath);
                return;
            }

            File[] jsonFiles = resourcePath.toFile().listFiles((_, name) -> name.toLowerCase().endsWith(".json"));
            if (jsonFiles == null || jsonFiles.length == 0) {
                log.warn("No JSON files found in {}. Skipping domain data seeding.", resourcePath);
                return;
            }

            log.info("Starting domain data seeding with {} files...", jsonFiles.length);
            processJsonFiles(jsonFiles);
            log.info("Domain data seeding completed successfully.");

        } catch (Exception e) {
            log.error("Error during domain data seeding", e);
        }
    }

    private void processJsonFiles(File[] files) {
        for (File file : files) {
            processJsonFile(file);
        }
    }

    @Transactional
    private void processJsonFile(File file) {
        try (InputStream inputStream = Files.newInputStream(file.toPath())) {
            log.info("Processing file: {}", file.getName());
            DomaineCreateRequestDto domaine = objectMapper.readValue(inputStream, DomaineCreateRequestDto.class);

            DomaineRequestDto requestDto = new DomaineRequestDto();
            requestDto.setNom(domaine.getNom());
            requestDto.setDescription(domaine.getDescription());
            Domaine createdDomaine = domaineService.create(requestDto);

            // if (domaine.getSousDomaines() != null) {
            // for (SousDomaineCreateRequestDto sousDomaineRequest :
            // domaine.getSousDomaines()) {
            // SousDomaine newSousDomaine = createSousDomaine(sousDomaineRequest,
            // createdDomaine);
            // processIndicateurs(sousDomaineRequest.getIndicateurs(), newSousDomaine);
            // }
            // }
        } catch (Exception e) {
            log.error("Error processing file {}: {}", file.getName(), e.getMessage());
        }
    }

    private SousDomaine createSousDomaine(SousDomaineCreateRequestDto request, Domaine parentDomaine) {
        SousDomaineRequestDto requestDto = new SousDomaineRequestDto();
        requestDto.setNom(request.getNom());
        requestDto.setDescription(request.getDescription());

        return sousDomaineService.create(parentDomaine.getId(), requestDto);
    }

    private void processIndicateurs(List<IndicateurCreateRequestDto> indicateurs, SousDomaine parentSousDomaine) {
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
    private void createIndicateur(IndicateurCreateRequestDto indicateurRequest, SousDomaine parentSousDomaine) {
        try {
            Indicateur newIndicateur = new Indicateur();
            newIndicateur.setNom(indicateurRequest.getNom());
            newIndicateur.setDescription(indicateurRequest.getDescription());
            newIndicateur.setAbreviation(indicateurRequest.getAbreviation());
            newIndicateur.setTypeTb(indicateurRequest.getTypeTb());
            newIndicateur.setUnite(indicateurRequest.getUnite());
            newIndicateur.setSource(indicateurRequest.getSource());
            newIndicateur.setRegleCalcul(indicateurRequest.getRegleCalcul());
            newIndicateur.setCategorie(indicateurRequest.getCategorie());

            newIndicateur.getSousDomaines().add(parentSousDomaine);

            Indicateur savedIndicateur = indicateurService.create(newIndicateur);

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

    @Transactional()
    private void handleDimensionInNewTransaction(DimensionCreateRequestDto dimensionRequest, Long indicateurId) {
        try {
            Indicateur indicateur = indicateurService.findById(indicateurId)
                    .orElseThrow(() -> new RuntimeException("Indicateur not found"));

            Dimension dimension = dimensionService.findByNom(dimensionRequest.getNom())
                    .orElseGet(() -> {
                        Dimension newDimension = new Dimension();
                        newDimension.setNom(dimensionRequest.getNom());
                        newDimension.setType(dimensionRequest.getType());
                        newDimension.setDescription("");
                        newDimension.setLibelle(dimensionRequest.getLibelle());

                        return dimensionService.save(newDimension);
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
}