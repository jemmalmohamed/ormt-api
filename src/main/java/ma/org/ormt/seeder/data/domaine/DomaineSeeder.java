package ma.org.ormt.seeder.data.domaine;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
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
import ma.org.ormt.modules.indicateurs.dimension.dtos.DomaineCreateRequestDto.IndicateurCreateRequestDto.IndicateurDonneeRequestDto;
import ma.org.ormt.modules.indicateurs.dimension.dtos.DomaineCreateRequestDto.SousDomaineCreateRequestDto;
import ma.org.ormt.modules.indicateurs.dimension.models.Dimension;
import ma.org.ormt.modules.indicateurs.dimension.services.DimensionService;
import ma.org.ormt.modules.indicateurs.indicateur.association.repository.IndicateurDimensionRepository;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;
import ma.org.ormt.modules.indicateurs.indicateur.models.IndicateurDimension;
import ma.org.ormt.modules.indicateurs.indicateur.services.IndicateurService;
import ma.org.ormt.modules.indicateurs.source.models.Source;
import ma.org.ormt.modules.indicateurs.source.services.SourceService;
import ma.org.ormt.modules.indicateurs.donnee.dtos.request.DonneeIndicateurRequestDto;
import ma.org.ormt.modules.indicateurs.donnee.services.DonneeIndicateurService;
import ma.org.ormt.modules.indicateurs.valeurdimension.dtos.request.ValeurDimensionRequestDto;

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
    private final DonneeIndicateurService donneeIndicateurService;

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
            createIndicateurDonnee(file);
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
    private Indicateur createIndicateur(IndicateurCreateRequestDto indicateurRequest, SousDomaine parentSousDomaine) {
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
            return indicateurService.findById(savedIndicateur.getId()).orElse(null);
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

    private void createIndicateurDonnee(File file) {

        File parentFolder = file.getParentFile();
        File dataFolder = new File(parentFolder, "data");

        if (dataFolder.exists() && dataFolder.isDirectory()) {
            File[] dataFiles = dataFolder
                    .listFiles((dir, name) -> name.toLowerCase().endsWith(".json") && new File(dir, name).isFile());

            if (dataFiles != null && dataFiles.length > 0) {
                log.info("Found {} data JSON files in folder: {}", dataFiles.length, dataFolder.getAbsolutePath());

                for (File dataFile : dataFiles) {
                    try (InputStream dataInputStream = Files.newInputStream(dataFile.toPath())) {
                        // Process each data file as needed
                        log.info("Processing data file: {}", dataFile.getName());
                        IndicateurDonneeRequestDto dataIndicareur = objectMapper.readValue(dataInputStream,
                                IndicateurDonneeRequestDto.class);

                        proccessDonneeIndicateur(dataIndicareur);

                    } catch (Exception e) {
                        log.error("Failed to process data file {}: {}", dataFile.getName(), e.getMessage(), e);
                    }
                }
            } else {
                log.debug("No data JSON files found in folder: {}", dataFolder.getAbsolutePath());
            }
        } else {
            log.debug("Data folder does not exist or is not a directory: {}", dataFolder.getAbsolutePath());
        }
    }

    private void proccessDonneeIndicateur(IndicateurDonneeRequestDto dataIndicareur) {
        try {
            Indicateur indicateur = indicateurService.findByNom(dataIndicareur.getIndicateur())
                    .orElseThrow(() -> new RuntimeException("Indicateur not found: " + dataIndicareur.getIndicateur()));

            List<Object> dataList = dataIndicareur.getData();
            log.info("Processing {} data entries for indicator: {}", dataList.size(), indicateur.getNom());

            for (Object dataItem : dataList) {
                try {
                    // Convert the data item to JsonNode for easier property access
                    JsonNode jsonNode = objectMapper.valueToTree(dataItem);

                    // Create a DonneeIndicateurRequestDto with the "valeur" property
                    DonneeIndicateurRequestDto donneeRequest = new DonneeIndicateurRequestDto();

                    // Extract the value property
                    if (jsonNode.has("valeur")) {
                        donneeRequest.setValeur(jsonNode.get("valeur").asText());
                    } else {
                        log.warn("Data item for indicator {} doesn't have 'valeur' property, skipping",
                                indicateur.getNom());
                        continue;
                    }

                    // Extract all other properties as dimension values
                    List<ValeurDimensionRequestDto> dimensionValues = new ArrayList<>();
                    Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();

                    while (fields.hasNext()) {
                        Map.Entry<String, JsonNode> field = fields.next();
                        String dimensionName = field.getKey();

                        // Skip the "valeur" field as it's already handled
                        if (!dimensionName.equals("valeur")) {
                            String dimensionValue = field.getValue().asText();

                            // Find or create the dimension
                            Dimension dimension = dimensionService.findByNom(dimensionName)
                                    .orElseThrow(() -> new RuntimeException("Dimension not found: " + dimensionName));

                            // Create ValeurDimensionRequestDto for this dimension
                            ValeurDimensionRequestDto valeurDimensionDto = new ValeurDimensionRequestDto();
                            valeurDimensionDto.setDimension(dimension);
                            valeurDimensionDto.setValeur(dimensionValue);

                            dimensionValues.add(valeurDimensionDto);
                        }
                    }

                    // Set the dimension values to the request
                    donneeRequest.setValeurDimensions(dimensionValues);

                    // Save the indicator data
                    donneeIndicateurService.create(indicateur.getId(), donneeRequest);

                } catch (Exception e) {
                    log.error("Error processing data item for indicator {}: {}", indicateur.getNom(), e.getMessage(),
                            e);
                }
            }

            log.info("Successfully processed {} data entries for indicator: {}", dataList.size(), indicateur.getNom());
        } catch (Exception e) {
            log.error("Failed to process data for indicator {}: {}", dataIndicareur.getIndicateur(), e.getMessage(), e);
        }
    }

    /**
     * Maps a JsonNode representing a data item to DonneeIndicateurRequestDto
     * 
     * @param dataItem JsonNode containing the data item properties
     * @return A populated DonneeIndicateurRequestDto
     */
    private DonneeIndicateurRequestDto mapToDonneeIndicateurRequest(JsonNode dataItem) {
        DonneeIndicateurRequestDto requestDto = new DonneeIndicateurRequestDto();

        // Extract value property (assuming it's always present)
        if (dataItem.has("valeur")) {
            requestDto.setValeur(dataItem.get("valeur").asText());
        } else {
            throw new IllegalArgumentException("Data item doesn't contain 'valeur' property");
        }

        // Extract all other properties as dimension values
        List<ValeurDimensionRequestDto> dimensionValues = new ArrayList<>();
        Iterator<Map.Entry<String, JsonNode>> fields = dataItem.fields();

        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            String key = field.getKey();

            // Skip the value field as it's already handled
            if (!key.equals("valeur")) {
                ValeurDimensionRequestDto dimensionValue = createDimensionValue(key, field.getValue().asText());
                dimensionValues.add(dimensionValue);
            }
        }

        requestDto.setValeurDimensions(dimensionValues);
        return requestDto;
    }

    /**
     * Creates a ValeurDimensionRequestDto for a given dimension name and value
     * 
     * @param dimensionName The name of the dimension
     * @param value         The value for the dimension
     * @return A populated ValeurDimensionRequestDto
     */
    private ValeurDimensionRequestDto createDimensionValue(String dimensionName, String value) {
        ValeurDimensionRequestDto requestDto = new ValeurDimensionRequestDto();

        // Find or create the dimension
        Dimension dimension = dimensionService.findByNom(dimensionName)
                .orElseGet(() -> {
                    Dimension newDimension = new Dimension();
                    newDimension.setNom(dimensionName);
                    newDimension.setType("string");
                    newDimension.setDescription("Created automatically during data import");
                    newDimension.setLibelle(dimensionName);
                    return dimensionService.save(newDimension);
                });

        // requestDto.setDimensionId(dimension.getId());
        requestDto.setValeur(value);

        return requestDto;
    }
}