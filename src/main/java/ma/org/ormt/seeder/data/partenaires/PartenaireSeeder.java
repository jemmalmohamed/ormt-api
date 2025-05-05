package ma.org.ormt.seeder.data.partenaires;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ma.org.ormt.core.utilities.FileToMultipartFileConverter;
import ma.org.ormt.modules.partenaires.partenaire.dtos.request.PartenaireRequestDto;
import ma.org.ormt.modules.partenaires.partenaire.models.Partenaire;
import ma.org.ormt.modules.partenaires.partenaire.services.PartenaireService;

@Log4j2
@Component
@Order(4)
@RequiredArgsConstructor
public class PartenaireSeeder implements CommandLineRunner {

    @Value("${starter.database.seed}")
    private boolean seeding;

    @Value("${data.external.data_path}")
    private String dataExternalPath;

    private final PartenaireService partenaireService;
    private final ObjectMapper objectMapper;

    private static final String IMAGES_SUBFOLDER = "images";
    private static final String PARTENAIRES_JSON_FILE = "partenaires.json";

    /**
     * Executes the partner data seeding process when the application starts.
     * Only runs if seeding is enabled in the configuration.
     *
     * @param args Command line arguments (not used)
     */
    @Override
    public void run(String... args) {
        if (!seeding) {
            log.info("Seeding is disabled. Skipping partenaire data seeding.");
            return;
        }

        try {
            String initDataPath = dataExternalPath + "/init-data/partenaires";
            log.info("Using data path: {}", initDataPath);

            Path resourcePath = Paths.get(initDataPath);
            if (!Files.exists(resourcePath)) {
                log.warn("Resource path {} does not exist. Skipping partenaire data seeding.", resourcePath);
                return;
            }

            File jsonFile = new File(resourcePath.toFile(), PARTENAIRES_JSON_FILE);
            if (!jsonFile.exists() || !jsonFile.isFile()) {
                log.warn("Partenaires JSON file not found at: {}", jsonFile.getAbsolutePath());
                return;
            }

            createPartenairesFromJsonFile(jsonFile, initDataPath);
            log.info("Partenaire data seeding completed successfully.");
        } catch (Exception e) {
            log.error("Error during partenaire data seeding: {}", e.getMessage(), e);
        }
    }

    /**
     * Creates partners from a JSON file.
     *
     * @param jsonFile     The JSON file containing partner data
     * @param initDataPath The base path for initialization data
     */
    @Transactional
    private void createPartenairesFromJsonFile(File jsonFile, String initDataPath) {
        try (InputStream inputStream = Files.newInputStream(jsonFile.toPath())) {
            log.info("Processing partenaires file: {}", jsonFile.getName());
            List<Partenaire> partenaireList = objectMapper.readValue(
                    inputStream,
                    new TypeReference<List<Partenaire>>() {
                    });

            if (partenaireList == null || partenaireList.isEmpty()) {
                log.warn("No partenaires found in file: {}", jsonFile.getName());
                return;
            }

            for (Partenaire partenaire : partenaireList) {
                try {
                    createPartenaire(partenaire, initDataPath);
                } catch (Exception e) {
                    log.error("Error creating partenaire {}: {}",
                            partenaire != null ? partenaire.getNom() : "unknown", e.getMessage(), e);
                }
            }

        } catch (IOException e) {
            log.error("Error reading partenaires file {}: {}", jsonFile.getName(), e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error processing partenaires file {}: {}", jsonFile.getName(), e.getMessage(), e);
        }
    }

    /**
     * Creates a single partner in the database.
     *
     * @param partenaire   The partner data from JSON
     * @param initDataPath The base path for initialization data
     * @throws IOException If there's an error processing the image file
     */
    private void createPartenaire(Partenaire partenaire, String initDataPath) throws IOException {
        if (partenaire == null || !StringUtils.hasText(partenaire.getNom())) {
            log.warn("Skipping invalid partenaire data: missing name");
            return;
        }

        // Check if partner already exists
        Optional<Partenaire> existingPartenaire = partenaireService.findByNom(partenaire.getNom());
        if (existingPartenaire.isPresent()) {
            log.info("Partenaire with name '{}' already exists. Skipping.", partenaire.getNom());
            return;
        }

        PartenaireRequestDto requestDto = new PartenaireRequestDto();
        requestDto.setNom(partenaire.getNom());
        requestDto.setDescription(partenaire.getDescription());
        requestDto.setSiteWebUrl(partenaire.getSiteWebUrl());

        // Process the image only if a imageUrl is provided
        if (StringUtils.hasText(partenaire.getImageUrl())) {
            // Look for the image in the images subfolder
            Path imagePath = Paths.get(initDataPath, IMAGES_SUBFOLDER, partenaire.getImageUrl());
            if (!Files.exists(imagePath)) {
                log.warn("Image not found at path: {}. Trying direct path.", imagePath);
                // Fallback to the main directory if not found in images subdirectory
                imagePath = Paths.get(initDataPath, partenaire.getImageUrl());
            }

            if (Files.exists(imagePath)) {
                MultipartFile imageFile = FileToMultipartFileConverter.toMultipartFile(imagePath.toFile());
                requestDto.setImageFile(imageFile);
                log.info("Found image for partenaire '{}' at: {}", partenaire.getNom(), imagePath);
            } else {
                log.error("Image for partenaire '{}' not found at: {}", partenaire.getNom(), imagePath);
            }
        }

        try {
            partenaireService.create(requestDto);
            log.info("Created partenaire: {}", partenaire.getNom());
        } catch (Exception e) {
            log.error("Error creating partenaire {}: {}", partenaire.getNom(), e.getMessage());
        }
    }
}