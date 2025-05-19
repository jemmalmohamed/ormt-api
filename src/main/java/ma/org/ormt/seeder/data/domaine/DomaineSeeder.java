package ma.org.ormt.seeder.data.domaine;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ma.org.ormt.modules.domaines.domaine.dtos.DomaineDto;
import ma.org.ormt.modules.domaines.domaine.dtos.request.DomaineRequestDto;
import ma.org.ormt.modules.domaines.domaine.models.Domaine;
import ma.org.ormt.modules.domaines.domaine.services.DomaineService;
import ma.org.ormt.modules.domaines.sousdomaine.dtos.request.SousDomaineRequestDto;
import ma.org.ormt.modules.domaines.sousdomaine.models.SousDomaine;
import ma.org.ormt.modules.domaines.sousdomaine.services.SousDomaineService;
import ma.org.ormt.modules.indicateurs.dimension.dtos.DomaineCreateRequestDto.IndicateurCreateRequestDto;
import ma.org.ormt.modules.indicateurs.dimension.dtos.DomaineCreateRequestDto.IndicateurCreateRequestDto.DimensionCreateRequestDto;
import ma.org.ormt.modules.indicateurs.dimension.dtos.DomaineCreateRequestDto.IndicateurCreateRequestDto.IndicateurDonneeRequestDto;
import ma.org.ormt.modules.indicateurs.dimension.dtos.DomaineCreateRequestDto.SousDomaineCreateRequestDto;
import ma.org.ormt.modules.indicateurs.dimension.models.Dimension;
import ma.org.ormt.modules.indicateurs.dimension.services.DimensionService;
import ma.org.ormt.modules.indicateurs.donnee.dtos.request.DonneeIndicateurRequestDto;
import ma.org.ormt.modules.indicateurs.donnee.services.DonneeIndicateurService;
import ma.org.ormt.modules.indicateurs.indicateur.association.repository.IndicateurDimensionRepository;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;
import ma.org.ormt.modules.indicateurs.indicateur.models.IndicateurDimension;
import ma.org.ormt.modules.indicateurs.indicateur.services.IndicateurService;
import ma.org.ormt.modules.indicateurs.valeurdimension.dtos.request.ValeurDimensionRequestDto;
import ma.org.ormt.core.utilities.FileToMultipartFileConverter;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Log4j2
@Component
@Order(3)
@RequiredArgsConstructor
public class DomaineSeeder implements CommandLineRunner {

    @Value("${starter.database.seed}")
    private boolean seeding;

    @Value("${data.external.data_path}")
    private String dataExternalPath;

    private final DomaineService domaineService;
    private final SousDomaineService sousDomaineService;
    private final IndicateurService indicateurService;
    private final DonneeIndicateurService donneeIndicateurService;
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
            // Process domains and their subdomains together
            processDomainesAndSousDomaines();

        } catch (Exception e) {
            log.error("Error during domain data seeding", e);
        }
    }

    private void processDomainesAndSousDomaines() {
        try {
            Path domainesPath = Paths.get(dataExternalPath + "/init-data/domaines/");
            if (!Files.exists(domainesPath)) {
                log.warn("Domaines path {} does not exist. Skipping domain data seeding.", domainesPath);
                return;
            }

            log.info("Starting to scan domain directories at: {}", domainesPath);

            // Get all subdirectories in the domaines folder - each represents a domain
            List<Path> domaineDirs = Files.list(domainesPath)
                    .filter(Files::isDirectory)
                    .collect(Collectors.toList());

            if (domaineDirs.isEmpty()) {
                log.warn("No domain directories found in {}. Skipping domain data seeding.", domainesPath);
                return;
            }

            // Process each domain directory
            for (Path domaineDir : domaineDirs) {
                String domaineDirName = domaineDir.getFileName().toString();
                log.info("Processing domain directory: {}", domaineDirName);

                // Find domain file (with .domaine.json extension)
                List<File> domaineFiles = Files.list(domaineDir)
                        .filter(path -> path.toString().toLowerCase().endsWith(".domaine.json"))
                        .map(Path::toFile)
                        .collect(Collectors.toList());

                // If no file with .domaine.json extension is found, look for any JSON file that
                // might be the domain file
                if (domaineFiles.isEmpty()) {
                    domaineFiles = Files.list(domaineDir)
                            .filter(path -> path.toString().toLowerCase().endsWith(".json"))
                            .filter(path -> !path.getParent().getFileName().toString().equals("sous-domaines"))
                            .map(Path::toFile)
                            .collect(Collectors.toList());
                }

                if (domaineFiles.isEmpty()) {
                    log.warn("No domain JSON file found in directory: {}. Skipping this domain.", domaineDirName);
                    continue;
                }

                // Process the first domain file found
                File domaineFile = domaineFiles.get(0);
                log.info("Found domain file: {}", domaineFile.getName());
                Domaine createdDomaine = processDomaineJsonFile(domaineFile);

                if (createdDomaine == null) {
                    log.warn("Failed to create domain from file: {}. Skipping subdomains.", domaineFile.getName());
                    continue;
                }

                // Look for sous-domaines folder
                Path sousDomainesDir = domaineDir.resolve("sous-domaines");
                if (Files.exists(sousDomainesDir) && Files.isDirectory(sousDomainesDir)) {
                    log.info("Found sous-domaines folder in domain: {}", domaineDirName);

                    // Get all subdirectories in the sous-domaines folder - each represents a
                    // subdomain
                    List<Path> sousDomaineDirs = Files.list(sousDomainesDir)
                            .filter(Files::isDirectory)
                            .collect(Collectors.toList());

                    if (!sousDomaineDirs.isEmpty()) {
                        log.info("Found {} subdomain directories in domain {}", sousDomaineDirs.size(), domaineDirName);

                        // Process each subdomain directory
                        for (Path sousDomainePath : sousDomaineDirs) {
                            String sousDomaineName = sousDomainePath.getFileName().toString();
                            log.info("Processing subdomain directory: {}", sousDomaineName);

                            // Find subdomain JSON file in this directory
                            List<File> sousDomaineFiles = Files.list(sousDomainePath)
                                    .filter(path -> path.toString().toLowerCase().endsWith(".json"))
                                    .map(Path::toFile)
                                    .collect(Collectors.toList());

                            if (!sousDomaineFiles.isEmpty()) {
                                File sousDomaineFile = sousDomaineFiles.get(0);
                                log.info("Found subdomain file: {}", sousDomaineFile.getName());
                                processSousDomaineJsonFile(sousDomaineFile, createdDomaine);
                            } else {
                                log.warn("No JSON file found in subdomain directory: {}", sousDomaineName);
                            }
                        }
                    } else {
                        log.info("No subdomain directories found in sous-domaines folder of domain {}", domaineDirName);
                    }
                } else {
                    log.info("No sous-domaines folder found in domain {}", domaineDirName);
                }
            }

            log.info("Domain and subdomain data seeding completed successfully.");
        } catch (Exception e) {
            log.error("Error processing domains and subdomains", e);
        }
    }

    @Transactional
    private Domaine processDomaineJsonFile(File file) {
        try (InputStream inputStream = Files.newInputStream(file.toPath())) {
            log.info("Processing domain file: {}", file.getName());
            DomaineDto domaine = objectMapper.readValue(inputStream, DomaineDto.class);

            DomaineRequestDto requestDto = new DomaineRequestDto();
            requestDto.setNom(domaine.getNom().toLowerCase());
            requestDto.setDescription(domaine.getDescription());
            requestDto.setApropos(domaine.getApropos());
            requestDto.setActif(domaine.getActif());

            handleDomaineImage(domaine, requestDto, file.getParent());

            Domaine createdDomaine = domaineService.create(requestDto);

            log.info("Created domain: {} with ID: {}", createdDomaine.getNom(), createdDomaine.getId());
            return createdDomaine;
        } catch (Exception e) {
            log.error("Error processing domain file {}: {}", file.getName(), e.getMessage());
            return null;
        }
    }

    private void handleDomaineImage(DomaineDto domaine, DomaineRequestDto requestDto,
            String domaineDirPath) throws IOException {
        if (StringUtils.hasText(domaine.getImageUrl())) {
            Path imagePath = Paths.get(domaineDirPath, domaine.getImageUrl());
            if (!Files.exists(imagePath)) {
                log.error("Image not found at path: {}. Trying direct path.", imagePath);
                imagePath = Paths.get(domaineDirPath, domaine.getImageUrl());
            }
            if (Files.exists(imagePath)) {
                MultipartFile imageFile = FileToMultipartFileConverter.toMultipartFile(imagePath.toFile());
                requestDto.setImageFile(imageFile);
                log.info("Found image for domaine '{}' at: {}", domaine.getNom(), imagePath);
            } else {
                log.error("Image for domaine '{}' not found at: {}", domaine.getNom(), imagePath);
            }
        }
    }

    @Transactional
    private void processSousDomaineJsonFile(File file, Domaine parentDomaine) {
        try (InputStream inputStream = Files.newInputStream(file.toPath())) {
            log.info("Processing subdomain file: {}", file.getName());
            SousDomaineCreateRequestDto sousDomaineData = objectMapper.readValue(inputStream,
                    SousDomaineCreateRequestDto.class);

            // Create the subdomain directly with the parent domain we already have
            SousDomaineRequestDto requestDto = new SousDomaineRequestDto();
            requestDto.setNom(sousDomaineData.getNom().toLowerCase());
            requestDto.setDescription(sousDomaineData.getDescription());
            requestDto.setActif(sousDomaineData.getActif());

            SousDomaine createdSousDomaine = sousDomaineService.create(parentDomaine.getId(), requestDto);
            log.info("Created subdomain: {} with ID: {} under domain: {}",
                    createdSousDomaine.getNom(), createdSousDomaine.getId(), parentDomaine.getNom());

            // Process indicateurs if they exist in the subdomain data
            if (sousDomaineData.getIndicateurs() != null && !sousDomaineData.getIndicateurs().isEmpty()) {
                processIndicateurs(sousDomaineData.getIndicateurs(), createdSousDomaine);
            }
            createIndicateurDonnee(file);

        } catch (Exception e) {
            log.error("Error processing subdomain file {}: {}", file.getName(), e.getMessage());
        }
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
                        newDimension.setNom(dimensionRequest.getNom().toLowerCase());
                        newDimension.setType(dimensionRequest.getType().toLowerCase());
                        newDimension.setDescription("");
                        newDimension.setLibelle(dimensionRequest.getLibelle().toLowerCase());

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
            Indicateur indicateur = indicateurService.findByNom(dataIndicareur.getIndicateur().toLowerCase())
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

}