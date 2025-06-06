package ma.org.ormt.seeder.data.sources;

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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ma.org.ormt.modules.indicateurs.source.dtos.request.SourceRequestDto;
import ma.org.ormt.modules.indicateurs.source.models.Source;
import ma.org.ormt.modules.indicateurs.source.services.SourceService;

@Log4j2
@Component
@Order(1)
@RequiredArgsConstructor
public class SourceSeeder implements CommandLineRunner {

    @Value("${starter.database.seed}")
    private boolean seeding;

    @Value("${data.external.data_path}")
    private String dataExternalPath;

    private final SourceService sourceService;
    private final ObjectMapper objectMapper;

    private static final String SOURCES_JSON_FILE = "sources.json";

    /**
     * Executes the partner data seeding process when the application starts.
     * Only runs if seeding is enabled in the configuration.
     *
     * @param args Command line arguments (not used)
     */
    @Override
    public void run(String... args) {
        if (!seeding) {
            log.info("Seeding is disabled. Skipping source data seeding.");
            return;
        }

        try {
            String initDataPath = dataExternalPath + "/init-data/sources";
            log.info("Using data path: {}", initDataPath);

            Path resourcePath = Paths.get(initDataPath);
            if (!Files.exists(resourcePath)) {
                log.warn("Resource path {} does not exist. Skipping source data seeding.", resourcePath);
                return;
            }

            File jsonFile = new File(resourcePath.toFile(), SOURCES_JSON_FILE);
            if (!jsonFile.exists() || !jsonFile.isFile()) {
                log.warn("Sources JSON file not found at: {}", jsonFile.getAbsolutePath());
                return;
            }

            createSourcesFromJsonFile(jsonFile, initDataPath);
            log.info("Source data seeding completed successfully.");
        } catch (Exception e) {
            log.error("Error during source data seeding: {}", e.getMessage(), e);
        }
    }

    /**
     * Creates partners from a JSON file.
     *
     * @param jsonFile     The JSON file containing partner data
     * @param initDataPath The base path for initialization data
     */
    @Transactional
    private void createSourcesFromJsonFile(File jsonFile, String initDataPath) {
        try (InputStream inputStream = Files.newInputStream(jsonFile.toPath())) {
            log.info("Processing sources file: {}", jsonFile.getName());
            List<Source> sourceList = objectMapper.readValue(
                    inputStream,
                    new TypeReference<List<Source>>() {
                    });

            if (sourceList == null || sourceList.isEmpty()) {
                log.warn("No sources found in file: {}", jsonFile.getName());
                return;
            }

            for (Source source : sourceList) {
                try {
                    createSource(source, initDataPath);
                } catch (Exception e) {
                    log.error("Error creating source {}: {}",
                            source != null ? source.getNom() : "unknown", e.getMessage(), e);
                }
            }

        } catch (IOException e) {
            log.error("Error reading sources file {}: {}", jsonFile.getName(), e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error processing sources file {}: {}", jsonFile.getName(), e.getMessage(), e);
        }
    }

    /**
     * Creates a single partner in the database.
     *
     * @param source       The partner data from JSON
     * @param initDataPath The base path for initialization data
     * @throws IOException If there's an error processing the image file
     */
    private void createSource(Source source, String initDataPath) throws IOException {
        if (source == null || !StringUtils.hasText(source.getNom())) {
            log.warn("Skipping invalid source data: missing name");
            return;
        }

        // Check if partner already exists
        Optional<Source> existingSource = sourceService.findByAbreviation(source.getAbreviation().toLowerCase());
        if (existingSource.isPresent()) {
            log.info("Source with abbreviation '{}' already exists. Skipping.", source.getAbreviation());
            return;
        }

        SourceRequestDto requestDto = new SourceRequestDto();
        requestDto.setNom(source.getNom());
        requestDto.setDescription(source.getDescription());
        requestDto.setUrl(source.getUrl());
        requestDto.setAbreviation(source.getAbreviation());

        try {
            sourceService.create(requestDto);
            log.info("Created source: {}", source.getNom());
        } catch (Exception e) {
            log.error("Error creating source {}: {}", source.getNom(), e.getMessage());
        }
    }
}