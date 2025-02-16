package ma.org.ormt.seeder.data.domaine;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ma.org.ormt.modules.dimension.models.Dimension;
import ma.org.ormt.modules.dimension.services.DimensionService;
import ma.org.ormt.modules.domaine.dtos.request.DomaineRequestDto;
import ma.org.ormt.modules.domaine.models.Domaine;
import ma.org.ormt.modules.domaine.services.DomaineService;
import ma.org.ormt.modules.indicateur.models.Indicateur;
import ma.org.ormt.modules.indicateur.services.IndicateurService;
import ma.org.ormt.modules.sousdomaine.dtos.request.SousDomaineRequestDto;
import ma.org.ormt.modules.sousdomaine.models.SousDomaine;
import ma.org.ormt.modules.sousdomaine.services.SousDomaineService;

@Log4j2
@Component
@Order(3)
@RequiredArgsConstructor
public class DomaineSeeder implements CommandLineRunner {

    private static final int THREAD_POOL_SIZE = 4;

    @Value("${starter.database.seed}")
    private boolean seeding;

    private final DomaineService domaineService;
    private final SousDomaineService sousDomaineService;
    private final IndicateurService indicateurService;
    private final DimensionService dimensionService;
    private final ObjectMapper objectMapper;
    private final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

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
        } finally {
            executorService.shutdown();
        }
    }

    private void processJsonFiles(File[] files) {
        CompletableFuture<?>[] futures = new CompletableFuture[files.length];
        for (int i = 0; i < files.length; i++) {
            final File file = files[i];
            futures[i] = CompletableFuture.runAsync(() -> processJsonFile(file), executorService);
        }
        CompletableFuture.allOf(futures).join();
    }

    @Transactional
    private void processJsonFile(File file) {
        try (InputStream inputStream = Files.newInputStream(file.toPath())) {
            log.info("Processing file: {}", file.getName());
            Domaine domaine = objectMapper.readValue(inputStream, Domaine.class);
            createDomainWithRelations(domaine);

        } catch (IOException e) {
            log.error("Error processing file {}: {}", file.getName(), e.getMessage());
        }
    }

    private void createDomainWithRelations(Domaine domaine) {
        try {
            DomaineRequestDto requestDto = new DomaineRequestDto();
            requestDto.setTitre(domaine.getTitre());
            requestDto.setDescription(domaine.getDescription());
            Domaine createdDomaine = domaineService.create(requestDto);

            processSousDomaines(domaine.getSousDomaines(), createdDomaine);

        } catch (Exception e) {
            log.error("Error creating domain {}: {}", domaine.getTitre(), e.getMessage());
        }
    }

    private void processSousDomaines(List<SousDomaine> sousDomaines, Domaine parentDomaine) {
        if (sousDomaines == null || sousDomaines.isEmpty()) {
            return;
        }

        sousDomaines.forEach(sousDomaineRequest -> {
            try {
                SousDomaine newSousDomaine = createSousDomaine(sousDomaineRequest, parentDomaine);
                processIndicateurs(sousDomaineRequest.getIndicateurs(), newSousDomaine);
            } catch (Exception e) {
                log.error("Error processing sous-domaine {}: {}", sousDomaineRequest.getTitre(), e.getMessage());
            }
        });
    }

    private SousDomaine createSousDomaine(SousDomaine request, Domaine parentDomaine) {
        SousDomaineRequestDto requestDto = new SousDomaineRequestDto();
        requestDto.setTitre(request.getTitre());
        requestDto.setDescription(request.getDescription());
        requestDto.setIdDomaine(parentDomaine.getId());

        return sousDomaineService.create(requestDto);
    }

    private void processIndicateurs(List<Indicateur> indicateurs, SousDomaine parentSousDomaine) {
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
    private void createIndicateur(Indicateur request, SousDomaine parentSousDomaine) {
        try {
            Indicateur newIndicateur = new Indicateur();
            newIndicateur.setNom(request.getNom());
            newIndicateur.setDescription(request.getDescription());
            newIndicateur.setAbreviation(request.getAbreviation());
            newIndicateur.setTypeTb(request.getTypeTb());
            newIndicateur.setUnite(request.getUnite());
            newIndicateur.setSource(request.getSource());
            newIndicateur.setRegleCalcul(request.getRegleCalcul());
            newIndicateur.setCategorie(request.getCategorie());
            newIndicateur.setSousDomaine(parentSousDomaine);
            newIndicateur.setDimensions(new ArrayList<>());

            // Save the indicateur first and flush to ensure it's in the database
            Indicateur savedIndicateur = indicateurService.create(newIndicateur);

            // Handle dimensions
            if (request.getDimensions() != null && !request.getDimensions().isEmpty()) {
                for (Dimension dimensionRequest : request.getDimensions()) {
                    try {
                        // First try to find existing dimension
                        Dimension dimension = dimensionService.findByNom(dimensionRequest.getNom())
                                .orElseGet(() -> {
                                    // Create and save new dimension if it doesn't exist
                                    Dimension newDimension = new Dimension();
                                    newDimension.setNom(dimensionRequest.getNom());
                                    newDimension.setType(dimensionRequest.getType());
                                    newDimension.setDescription(dimensionRequest.getDescription());
                                    newDimension.setLibelle(dimensionRequest.getLibelle());
                                    newDimension.setIndicateurs(new ArrayList<>());
                                    return dimensionService.save(newDimension); // Use save instead of create
                                });
                        // Add bidirectional relationship
                        dimension.getIndicateurs().add(savedIndicateur);
                        // Save both entities
                        dimensionService.save(dimension);
                    } catch (Exception e) {
                        log.error("Error handling dimension {}: {}", dimensionRequest.getNom(), e.getMessage());
                        throw e; // Rethrow to rollback transaction
                    }
                }
            }

            log.info("Created indicateur: {} with dimensions", savedIndicateur.getNom());
        } catch (Exception e) {
            log.error("Error in createIndicateur: {}", e.getMessage());
            throw new RuntimeException("Failed to create indicateur", e);
        }
    }
}