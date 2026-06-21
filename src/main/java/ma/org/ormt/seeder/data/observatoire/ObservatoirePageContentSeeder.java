package ma.org.ormt.seeder.data.observatoire;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ma.org.ormt.core.utilities.files.FileDataService;
import ma.org.ormt.modules.observatoire.dtos.request.ObservatoirePageContentRequestDto;
import ma.org.ormt.modules.observatoire.services.ObservatoirePageContentService;

@Log4j2
@Component
@Order(4)
@RequiredArgsConstructor
public class ObservatoirePageContentSeeder implements CommandLineRunner {

    private static final String OBSERVATOIRE_JSON_FILE = "observatoire-page-content.json";

    @Value("${starter.database.seed}")
    private boolean seeding;

    @Value("${data.external.data_path}")
    private String dataExternalPath;

    private final ObservatoirePageContentService observatoirePageContentService;
    private final FileDataService fileDataService;

    @Override
    public void run(String... args) {
        if (!seeding) {
            log.info("Seeding is disabled. Skipping observatoire content seeding.");
            return;
        }

        if (observatoirePageContentService.findCurrent().isPresent()) {
            log.info("Observatoire content already exists. Skipping seeding.");
            return;
        }

        try {
            String initDataPath = dataExternalPath + "/init-data/observatoire";
            Path resourcePath = Paths.get(initDataPath);
            if (!Files.exists(resourcePath)) {
                log.warn("Resource path {} does not exist. Skipping observatoire content seeding.", resourcePath);
                return;
            }

            File jsonFile = new File(resourcePath.toFile(), OBSERVATOIRE_JSON_FILE);
            if (!fileDataService.fileExists(jsonFile)) {
                log.warn("Observatoire JSON file not found at: {}", jsonFile.getAbsolutePath());
                return;
            }

            ObservatoirePageContentRequestDto requestDto = fileDataService.readJsonFile(
                    jsonFile,
                    ObservatoirePageContentRequestDto.class);

            if (requestDto.getActif() == null) {
                requestDto.setActif(true);
            }
            if (requestDto.getPublished() == null) {
                requestDto.setPublished(true);
            }

            observatoirePageContentService.create(requestDto);
            log.info("Observatoire content seeded successfully from {}", jsonFile.getAbsolutePath());
        } catch (Exception exception) {
            log.error("Error during observatoire content seeding: {}", exception.getMessage(), exception);
        }
    }
}