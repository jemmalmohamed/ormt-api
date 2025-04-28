package ma.org.ormt.seeder.data.domaine;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ma.org.ormt.core.utilities.FileUtils;
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

    @Value("${data.external.data_path}")
    private String dataExternalPath;

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
            DomaineCreateRequestDto domaine = objectMapper.readValue(inputStream, DomaineCreateRequestDto.class);

            DomaineRequestDto requestDto = new DomaineRequestDto();
            requestDto.setNom(domaine.getNom());
            requestDto.setDescription(domaine.getDescription());
            requestDto.setApropos(domaine.getApropos());
            requestDto.setRole(domaine.getRole());
            requestDto.setStatut(domaine.getStatut());
            Domaine createdDomaine = domaineService.create(requestDto);

            log.info("Created domain: {} with ID: {}", createdDomaine.getNom(), createdDomaine.getId());
            return createdDomaine;
        } catch (Exception e) {
            log.error("Error processing domain file {}: {}", file.getName(), e.getMessage());
            return null;
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
            requestDto.setNom(sousDomaineData.getNom());
            requestDto.setDescription(sousDomaineData.getDescription());
            requestDto.setRole(sousDomaineData.getRole());
            requestDto.setStatut(sousDomaineData.getStatut());

            SousDomaine createdSousDomaine = sousDomaineService.create(parentDomaine.getId(), requestDto);
            log.info("Created subdomain: {} with ID: {} under domain: {}",
                    createdSousDomaine.getNom(), createdSousDomaine.getId(), parentDomaine.getNom());

            // Process indicateurs if they exist in the subdomain data
            if (sousDomaineData.getIndicateurs() != null && !sousDomaineData.getIndicateurs().isEmpty()) {
                processIndicateurs(sousDomaineData.getIndicateurs(), createdSousDomaine);
            }
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
            newIndicateur.setNom(indicateurRequest.getNom());
            newIndicateur.setCategorie(indicateurRequest.getCategorie());
            newIndicateur.setRole(indicateurRequest.getRole());
            newIndicateur.setStatut(indicateurRequest.getStatut());
            newIndicateur.setAbreviation(indicateurRequest.getAbreviation());
            newIndicateur.setTypeTb(indicateurRequest.getTypeTb());
            newIndicateur.setRegleCalcul(indicateurRequest.getRegleCalcul());
            newIndicateur.setUnite(indicateurRequest.getUnite());
            newIndicateur.setDescription(indicateurRequest.getDescription());

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