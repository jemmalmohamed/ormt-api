package ma.org.ormt.seeder.data.domaine;

import java.io.File;
import java.io.IOException;
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
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ma.org.ormt.core.utilities.files.FileToMultipartFileConverter;
import ma.org.ormt.modules.domaines.domaine.dtos.DomaineDto;
import ma.org.ormt.modules.domaines.domaine.dtos.request.DomaineRequestDto;
import ma.org.ormt.modules.domaines.domaine.models.Domaine;
import ma.org.ormt.modules.domaines.domaine.services.DomaineService;
import ma.org.ormt.modules.domaines.sousdomaine.dtos.request.SousDomaineRequestDto;
import ma.org.ormt.modules.domaines.sousdomaine.models.SousDomaine;
import ma.org.ormt.modules.domaines.sousdomaine.services.SousDomaineService;
import ma.org.ormt.modules.indicateurs.dimension.dtos.DomaineCreateRequestDto.SousDomaineCreateRequestDto;

import ma.org.ormt.seeder.data.donnee.DonneeIndicateurSeeder;
import ma.org.ormt.seeder.data.indicateur.IndicateurSeeder;

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
    private final ObjectMapper objectMapper;;
    private final DonneeIndicateurSeeder donneeIndicateurSeeder;
    private final IndicateurSeeder indicateurSeeder;

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

            List<Path> domaineDirs = Files.list(domainesPath)
                    .filter(Files::isDirectory)
                    .collect(Collectors.toList());

            if (domaineDirs.isEmpty()) {
                log.warn("No domain directories found in {}. Skipping domain data seeding.", domainesPath);
                return;
            }

            // Process each domain directory sequentially (no threading)
            for (Path domaineDir : domaineDirs) {
                processSingleDomaineDir(domaineDir);
            }

            log.info("Domain and subdomain data seeding completed successfully.");
        } catch (Exception e) {
            log.error("Error processing domains and subdomains", e);
        }
    }

    private void processSingleDomaineDir(Path domaineDir) {
        String domaineDirName = domaineDir.getFileName().toString();
        log.info("Processing domain directory: {}", domaineDirName);
        try {
            File domaineFile = findDomaineFile(domaineDir);
            if (domaineFile == null) {
                log.warn("No domain JSON file found in directory: {}. Skipping this domain.", domaineDirName);
                return;
            }
            log.info("Found domain file: {}", domaineFile.getName());
            Domaine createdDomaine = processDomaineJsonFile(domaineFile);
            if (createdDomaine == null) {
                log.warn("Failed to create domain from file: {}. Skipping subdomains.", domaineFile.getName());
                return;
            }
            Path sousDomainesDir = domaineDir.resolve("sous-domaines");
            if (Files.exists(sousDomainesDir) && Files.isDirectory(sousDomainesDir)) {
                List<Path> sousDomaineDirs = Files.list(sousDomainesDir)
                        .filter(Files::isDirectory)
                        .collect(Collectors.toList());
                if (!sousDomaineDirs.isEmpty()) {
                    // Process each subdomain directory sequentially (no threading)
                    for (Path sousDomainePath : sousDomaineDirs) {
                        processSingleSousDomaineDir(sousDomainePath, createdDomaine);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error processing domain directory {}: {}", domaineDirName, e.getMessage(), e);
        }
    }

    private File findDomaineFile(Path domaineDir) throws IOException {
        List<File> domaineFiles = Files.list(domaineDir)
                .filter(path -> path.toString().toLowerCase().endsWith(".domaine.json"))
                .map(Path::toFile)
                .collect(Collectors.toList());
        if (domaineFiles.isEmpty()) {
            domaineFiles = Files.list(domaineDir)
                    .filter(path -> path.toString().toLowerCase().endsWith(".json"))
                    .filter(path -> !path.getParent().getFileName().toString().equals("sous-domaines"))
                    .map(Path::toFile)
                    .collect(Collectors.toList());
        }
        return domaineFiles.isEmpty() ? null : domaineFiles.get(0);
    }

    private void processSingleSousDomaineDir(Path sousDomainePath, Domaine createdDomaine) {
        String sousDomaineName = sousDomainePath.getFileName().toString();
        log.info("Processing subdomain directory: {}", sousDomaineName);
        try {
            File sousDomaineFile = Files.list(sousDomainePath)
                    .filter(path -> path.toString().toLowerCase().endsWith(".json"))
                    .map(Path::toFile)
                    .findFirst().orElse(null);
            if (sousDomaineFile != null) {
                log.info("Found subdomain file: {}", sousDomaineFile.getName());
                processSousDomaineJsonFile(sousDomaineFile, createdDomaine);
            } else {
                log.warn("No JSON file found in subdomain directory: {}", sousDomaineName);
            }
        } catch (Exception e) {
            log.error("Error processing subdomain directory {}: {}", sousDomaineName, e.getMessage(), e);
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
                indicateurSeeder.processIndicateurs(sousDomaineData.getIndicateurs(), createdSousDomaine);
            }

            donneeIndicateurSeeder.createIndicateurDonnee(file);

        } catch (Exception e) {
            log.error("Error processing subdomain file {}: {}", file.getName(), e.getMessage());
        }
    }

}