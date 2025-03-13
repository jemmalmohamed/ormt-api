package ma.org.ormt.seeder.data.domaine;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
import ma.org.ormt.modules.indicateurs.source.models.Source;
import ma.org.ormt.modules.indicateurs.source.services.SourceService;

/**
 * DomaineSeeder is responsible for initializing domain data in the application.
 * This seeder reads JSON files from a specified directory structure and creates
 * the following hierarchy of entities:
 * - Domaines (Domains)
 * - Sous-Domaines (Sub-domains)
 * - Indicateurs (Indicators)
 * - Dimensions
 *
 * Directory structure expected:
 * src/main/resources/init-data/domaines/
 * ├── domain1/
 * │ ├── domain.json
 * │ └── sous-domaines/
 * │ └── subdomain1/
 * │ └── subdomain.json
 * └── domain2/
 * └── ...
 */
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
    private final SourceService sourceService;
    private final ObjectMapper objectMapper;
    private final IndicateurDimensionRepository indicateurDimensionRepository;

    private static final String INIT_DATA_PATH = "src/main/resources/init-data/domaines";
    private static final String SOUS_DOMAINES_FOLDER = "sous-domaines";

    /**
     * Executes the domain data seeding process when the application starts.
     * Only runs if seeding is enabled in the configuration.
     *
     * @param args Command line arguments (not used)
     */
    @Override
    public void run(String... args) {
        if (!seeding) {
            log.info("Seeding is disabled. Skipping domain data seeding.");
            return;
        }

        try {
            Path resourcePath = Paths.get(INIT_DATA_PATH);
            if (!Files.exists(resourcePath)) {
                log.warn("Resource path {} does not exist. Skipping domain data seeding.", resourcePath);
                return;
            }

            processMainFolderDomains(resourcePath.toFile());
            log.info("Domain data seeding completed successfully.");
        } catch (Exception e) {
            log.error("Error during domain data seeding", e);
        }
    }

    /**
     * Processes the main domains folder and initiates the creation of domains.
     *
     * @param mainFolder The root folder containing domain data
     */
    private void processMainFolderDomains(File mainFolder) {
        if (!validateFolder(mainFolder)) {
            return;
        }

        try {
            File[] domaineFolders = mainFolder.listFiles(File::isDirectory);
            if (domaineFolders != null && domaineFolders.length > 0) {
                log.info("Found {} domain folders in: {}", domaineFolders.length, mainFolder.getAbsolutePath());
                for (File domaineFolder : domaineFolders) {
                    processDomaineFolder(domaineFolder);
                }
            }
        } catch (SecurityException e) {
            log.error("Security error accessing folder {}: {}", mainFolder.getAbsolutePath(), e.getMessage(), e);
        }
    }

    /**
     * Validates if a folder exists and is actually a directory.
     *
     * @param folder The folder to validate
     * @return true if the folder is valid, false otherwise
     */
    private boolean validateFolder(File folder) {
        if (folder == null || !folder.exists()) {
            log.error("Folder is null or does not exist");
            return false;
        }
        if (!folder.isDirectory()) {
            log.error("Specified path {} is not a directory", folder.getAbsolutePath());
            return false;
        }
        return true;
    }

    /**
     * Processes a single domain folder, creating the domain and its sub-domains.
     *
     * @param folder The domain folder to process
     */
    private void processDomaineFolder(File folder) {
        File[] jsonFiles = folder
                .listFiles((dir, name) -> name.toLowerCase().endsWith(".json") && new File(dir, name).isFile());

        if (jsonFiles != null && jsonFiles.length > 0) {
            log.info("Found {} JSON files in folder: {}", jsonFiles.length, folder.getAbsolutePath());

            for (File file : jsonFiles) {
                try {
                    Domaine newDomaine = createDomaineFromJsonFile(file);
                    if (newDomaine != null) {
                        processSousDomaineFolders(folder, newDomaine);
                    }
                } catch (Exception e) {
                    log.error("Failed to process file {}: {}", file.getName(), e.getMessage(), e);
                }
            }
        } else {
            log.debug("No JSON files found in folder: {}", folder.getAbsolutePath());
        }
    }

    @Transactional
    private Domaine createDomaineFromJsonFile(File file) {

        try (InputStream inputStream = Files.newInputStream(file.toPath())) {
            log.info("Processing domain file: {}", file.getName());
            DomaineCreateRequestDto domaine = objectMapper.readValue(inputStream, DomaineCreateRequestDto.class);

            DomaineRequestDto requestDto = new DomaineRequestDto();
            requestDto.setNom(domaine.getNom());
            requestDto.setDescription(domaine.getDescription());
            requestDto.setApropos(domaine.getApropos());
            requestDto.setRole(domaine.getRole());
            requestDto.setStatut(domaine.getStatut());
            Domaine createdDomaine = domaineService.create(requestDto);

            // Store the created domain for later use with subfolders

            log.info("Created domain: {}", createdDomaine.getNom());
            return createdDomaine;

        } catch (Exception e) {
            log.error("Error processing domain file {}: {}", file.getName(), e.getMessage());
        }
        return null;
    }

    /**
     * Processes the sous-domaines (sub-domains) folder for a given domain.
     *
     * @param domaineFolder The parent domain folder
     * @param parentDomaine The parent domain entity
     */
    @Transactional
    private void processSousDomaineFolders(File domaineFolder, Domaine parentDomaine) {
        File specificSubfolder = new File(domaineFolder, SOUS_DOMAINES_FOLDER);
        File[] sousDomaineFolders = specificSubfolder.listFiles(File::isDirectory);

        if (sousDomaineFolders != null && sousDomaineFolders.length > 0) {
            log.info("Processing {} sub-domains in domain: {}",
                    sousDomaineFolders.length, parentDomaine.getNom());
            for (File subFolder : sousDomaineFolders) {
                processSousDomaineFolder(subFolder, parentDomaine);
            }
        }
    }

    private void processSousDomaineFolder(File folder, Domaine parentDomaine) {
        File[] jsonFiles = folder.listFiles((dir, name) -> {
            String lowerCaseName = name.toLowerCase();
            return lowerCaseName.endsWith(".json") && new File(dir, name).isFile();
        });

        if (jsonFiles != null && jsonFiles.length > 0) {
            log.info("Found {} JSON files in folder: {}",
                    jsonFiles.length, folder.getAbsolutePath());

            for (File file : jsonFiles) {
                try {
                    SousDomaine newSousDomaine = createSousDomaineFromJsonFile(file, parentDomaine);
                    if (newSousDomaine != null) {
                        // processSousDomaineFolders(folder, newDomaine);
                    }
                } catch (Exception e) {
                    log.error("Failed to process file {}: {}",
                            file.getName(), e.getMessage(), e);
                }
            }
        } else {
            log.debug("No JSON files found in folder: {}",
                    folder.getAbsolutePath());
        }
    }

    private SousDomaine createSousDomaineFromJsonFile(File file, Domaine parentDomaine) {
        try (InputStream inputStream = Files.newInputStream(file.toPath())) {
            log.info("Processing sous-domaine file: {}", file.getName());
            SousDomaineCreateRequestDto souDomaineData = objectMapper.readValue(inputStream,
                    SousDomaineCreateRequestDto.class);

            SousDomaine newSousDomaine = createSousDomaine(souDomaineData, parentDomaine);
            souDomaineData.getIndicateurs().forEach(indicateurRequest -> {
                try {
                    createIndicateur(indicateurRequest, newSousDomaine);
                } catch (Exception e) {
                    log.error("Error creating indicateur {}: {}", indicateurRequest.getNom(), e.getMessage());
                }
            });

        } catch (Exception e) {
            // log.error("Error processing sous-domaine file {}: {}", file.getName(),
            // e.getMessage());
        }
        return null;
    }

    private SousDomaine createSousDomaine(SousDomaineCreateRequestDto request, Domaine parentDomaine) {
        SousDomaineRequestDto requestDto = new SousDomaineRequestDto();
        requestDto.setNom(request.getNom());
        requestDto.setDescription(request.getDescription());
        requestDto.setRole(request.getRole());
        requestDto.setStatut(request.getStatut());

        return sousDomaineService.create(parentDomaine.getId(), requestDto);
    }

    @Transactional
    private void createIndicateur(IndicateurCreateRequestDto indicateurRequest, SousDomaine parentSousDomaine) {
        try {
            Indicateur newIndicateur = new Indicateur();
            newIndicateur.setNom(indicateurRequest.getNom());
            newIndicateur.setDescription(indicateurRequest.getDescription());
            newIndicateur.setRole(indicateurRequest.getRole());
            newIndicateur.setStatut(indicateurRequest.getStatut());
            newIndicateur.setCategorie(indicateurRequest.getCategorie());
            newIndicateur.setAbreviation(indicateurRequest.getAbreviation());
            newIndicateur.setTypeTb(indicateurRequest.getTypeTb());
            newIndicateur.setUnite(indicateurRequest.getUnite());

            newIndicateur.setRegleCalcul(indicateurRequest.getRegleCalcul());
            newIndicateur.setCategorie(indicateurRequest.getCategorie());

            String sourceName = indicateurRequest.getSource();
            // find or create source
            Source source = getSourceOrCreate(sourceName);
            newIndicateur.setSource(source);

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

    /**
     * Creates an indicator and its associated dimensions in a new transaction.
     *
     * @param dimensionRequest The dimension creation request
     * @param indicateurId     The ID of the parent indicator
     */
    @Transactional
    private void handleDimensionInNewTransaction(DimensionCreateRequestDto dimensionRequest, Long indicateurId) {
        try {
            Indicateur indicateur = indicateurService.findById(indicateurId)
                    .orElseThrow(() -> new RuntimeException("Indicateur not found"));

            Dimension dimension = getDimensionOrCreate(dimensionRequest);
            createIndicateurDimensionAssociation(indicateur, dimension, dimensionRequest);

        } catch (Exception e) {
            log.error("Error handling dimension {}: {}", dimensionRequest.getNom(), e.getMessage());
            throw e;
        }
    }

    /**
     * Gets an existing dimension or creates a new one if it doesn't exist.
     *
     * @param request The dimension creation request
     * @return The found or created dimension
     */
    private Dimension getDimensionOrCreate(DimensionCreateRequestDto request) {
        return dimensionService.findByNom(request.getNom())
                .orElseGet(() -> {
                    Dimension newDimension = new Dimension();
                    newDimension.setNom(request.getNom());
                    newDimension.setType(request.getType());
                    newDimension.setDescription("");
                    newDimension.setLibelle(request.getLibelle());
                    return dimensionService.save(newDimension);
                });
    }

    /**
     * Gets an existing dimension or creates a new one if it doesn't exist.
     *
     * @param request The dimension creation request
     * @return The found or created dimension
     */
    private Source getSourceOrCreate(String sourceName) {
        return sourceService.findByNom(sourceName.toLowerCase())
                .orElseGet(() -> {
                    Source newSource = new Source();
                    newSource.setNom(sourceName);
                    newSource.setDescription("");
                    newSource.setRole("public");
                    newSource.setStatut("actif");
                    return sourceService.save(newSource);
                });
    }

    /**
     * Creates the association between an indicator and a dimension.
     *
     * @param indicateur The indicator entity
     * @param dimension  The dimension entity
     * @param request    The dimension creation request containing association
     *                   details
     */
    private void createIndicateurDimensionAssociation(
            Indicateur indicateur,
            Dimension dimension,
            DimensionCreateRequestDto request) {

        IndicateurDimension indicateurDimension = new IndicateurDimension();
        indicateurDimension.setIndicateur(indicateur);
        indicateurDimension.setDimension(dimension);
        indicateurDimension.setPrincipale(request.getAssociation().getPrincipale());
        indicateurDimension.setTemporelle(request.getAssociation().getTemporelle());

        indicateurDimensionRepository.save(indicateurDimension);
    }
}