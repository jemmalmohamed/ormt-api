package ma.org.ormt.seeder.data.chiffres;

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
import ma.org.ormt.modules.chiffres.dtos.request.ChiffreCleRequestDto;
import ma.org.ormt.modules.chiffres.models.ChiffreCle;
import ma.org.ormt.modules.chiffres.services.ChiffreCleService;

@Log4j2
@Component
@Order(4)
@RequiredArgsConstructor
public class ChiffreCleSeeder implements CommandLineRunner {

    @Value("${starter.database.seed}")
    private boolean seeding;

    @Value("${data.external.data_path}")
    private String dataExternalPath;

    private final ChiffreCleService chiffreCleService;
    private final ObjectMapper objectMapper;

    private static final String CHIFFRE_CLE_JSON_FILE = "chiffre_cle.json";

    /**
     * Executes the partner data seeding process when the application starts.
     * Only runs if seeding is enabled in the configuration.
     *
     * @param args Command line arguments (not used)
     */
    @Override
    public void run(String... args) {
        if (!seeding) {
            log.info("Seeding is disabled. Skipping chiffreCle data seeding.");
            return;
        }

        try {
            String initDataPath = dataExternalPath + "/init-data/chiffres";
            log.info("Using data path: {}", initDataPath);

            Path resourcePath = Paths.get(initDataPath);
            if (!Files.exists(resourcePath)) {
                log.warn("Resource path {} does not exist. Skipping chiffreCle data seeding.", resourcePath);
                return;
            }

            File jsonFile = new File(resourcePath.toFile(), CHIFFRE_CLE_JSON_FILE);
            if (!jsonFile.exists() || !jsonFile.isFile()) {
                log.warn("ChiffreCles JSON file not found at: {}", jsonFile.getAbsolutePath());
                return;
            }

            createChiffreClesFromJsonFile(jsonFile, initDataPath);
            log.info("ChiffreCle data seeding completed successfully.");
        } catch (Exception e) {
            log.error("Error during chiffreCle data seeding: {}", e.getMessage(), e);
        }
    }

    /**
     * Creates partners from a JSON file.
     *
     * @param jsonFile     The JSON file containing partner data
     * @param initDataPath The base path for initialization data
     */
    @Transactional
    private void createChiffreClesFromJsonFile(File jsonFile, String initDataPath) {
        try (InputStream inputStream = Files.newInputStream(jsonFile.toPath())) {
            log.info("Processing chiffreCles file: {}", jsonFile.getName());
            List<ChiffreCle> chiffreCleList = objectMapper.readValue(
                    inputStream,
                    new TypeReference<List<ChiffreCle>>() {
                    });

            if (chiffreCleList == null || chiffreCleList.isEmpty()) {
                log.warn("No chiffreCles found in file: {}", jsonFile.getName());
                return;
            }

            for (ChiffreCle chiffreCle : chiffreCleList) {
                try {
                    createChiffreCle(chiffreCle, initDataPath);
                } catch (Exception e) {
                    log.error("Error creating chiffreCle {}: {}",
                            chiffreCle != null ? chiffreCle.getLibelle() : "unknown", e.getMessage(), e);
                }
            }

        } catch (IOException e) {
            log.error("Error reading chiffreCles file {}: {}", jsonFile.getName(), e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error processing chiffreCles file {}: {}", jsonFile.getName(), e.getMessage(), e);
        }
    }

    /**
     * Creates a single partner in the database.
     *
     * @param chiffreCle   The partner data from JSON
     * @param initDataPath The base path for initialization data
     * @throws IOException If there's an error processing the image file
     */
    private void createChiffreCle(ChiffreCle chiffreCle, String initDataPath) throws IOException {
        if (chiffreCle == null || !StringUtils.hasText(chiffreCle.getLibelle())) {
            log.warn("Skipping invalid chiffreCle data: missing name");
            return;
        }

        // Check if partner already exists
        Optional<ChiffreCle> existingChiffreCle = chiffreCleService.findByLibelle(chiffreCle.getLibelle());
        if (existingChiffreCle.isPresent()) {
            log.info("ChiffreCle with name '{}' already exists. Skipping.", chiffreCle.getLibelle());
            return;
        }

        ChiffreCleRequestDto requestDto = new ChiffreCleRequestDto();
        requestDto.setLibelle(chiffreCle.getLibelle());
        requestDto.setDescription(chiffreCle.getDescription());
        requestDto.setValeur(chiffreCle.getValeur());
        requestDto.setUnite(chiffreCle.getUnite());
        requestDto.setActif(chiffreCle.getActif());

        try {
            chiffreCleService.create(requestDto);
            log.info("Created chiffreCle: {}", chiffreCle.getLibelle());
        } catch (Exception e) {
            log.error("Error creating chiffreCle {}: {}", chiffreCle.getLibelle(), e.getMessage());
        }
    }
}