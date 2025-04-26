package ma.org.ormt.seeder.data.partenaires;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
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
import ma.org.ormt.core.minio.MinioService;
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

    private final PartenaireService partenaireService;
    private final ObjectMapper objectMapper;

    private static final String INIT_DATA_PATH = "src/main/resources/init-data/partenaires";

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
            Path resourcePath = Paths.get(INIT_DATA_PATH);
            if (!Files.exists(resourcePath)) {
                log.warn("Resource path {} does not exist. Skipping partenaire data seeding.", resourcePath);
                return;
            }

            processPartenaireJsonFiles(resourcePath.toFile());
            log.info("Partenaire data seeding completed successfully.");
        } catch (Exception e) {
            log.error("Error during partenaire data seeding: {}", e.getMessage(), e);
        }
    }

    /**
     * Processes partner JSON files and seeds the data.
     *
     * @param folder The folder containing partner JSON files
     */
    private void processPartenaireJsonFiles(File folder) {
        File[] jsonFiles = folder
                .listFiles((dir, name) -> name.toLowerCase().endsWith(".json") && new File(dir, name).isFile());

        if (jsonFiles == null || jsonFiles.length == 0) {
            log.warn("No JSON files found in folder: {}", folder.getAbsolutePath());
            return;
        }

        log.info("Found {} JSON files in folder: {}", jsonFiles.length, folder.getAbsolutePath());
        Arrays.stream(jsonFiles)
                .forEach(file -> {
                    try {
                        createPartenairesFromJsonFile(file);
                    } catch (Exception e) {
                        log.error("Failed to process file {}: {}", file.getName(), e.getMessage(), e);
                    }
                });
    }

    /**
     * Creates partners from a JSON file.
     *
     * @param jsonFile The JSON file containing partner data
     */
    @Transactional
    private void createPartenairesFromJsonFile(File jsonFile) {
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

                    createPartenaire(partenaire);

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
     * @param partenaire The partner data from JSON
     * @throws IOException If there's an error processing the image file
     */
    private void createPartenaire(Partenaire partenaire) throws IOException {
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
            // Fallback to the main directory if not found in images subdirectory
            Path imagePath = Paths.get(INIT_DATA_PATH, partenaire.getImageUrl());
            MultipartFile imageFile = FileToMultipartFileConverter.toMultipartFile(imagePath.toFile());

            requestDto.setImageFile(imageFile);

        }

        try {
            partenaireService.create(requestDto);
            log.info("Created partenaire: {}", partenaire.getNom());
        } catch (Exception e) {
            log.error("Error creating partenaire {}: {}", partenaire.getNom(), e.getMessage());

        }
    }

}