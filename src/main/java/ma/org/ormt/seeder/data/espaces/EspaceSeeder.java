package ma.org.ormt.seeder.data.espaces;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

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
import ma.org.ormt.modules.domaines.domaine.models.Domaine;
import ma.org.ormt.modules.domaines.domaine.services.DomaineService;
import ma.org.ormt.modules.espaces.dtos.EspaceDto;
import ma.org.ormt.modules.espaces.dtos.request.EspaceRequestDto;
import ma.org.ormt.modules.espaces.models.Espace;
import ma.org.ormt.modules.espaces.services.EspaceService;
import ma.org.ormt.security.roleacces.services.RoleAccesService;

@Log4j2
@Component
@Order(4)
@RequiredArgsConstructor
public class EspaceSeeder implements CommandLineRunner {

    @Value("${starter.database.seed}")
    private boolean seeding;

    private final EspaceService espaceService;

    private final DomaineService domaineService;

    private final RoleAccesService roleAccesService;

    private final ObjectMapper objectMapper;

    private static final String INIT_DATA_PATH = "src/main/resources/init-data/espaces";

    /**
     * Executes the domain data seeding process when the application starts.
     * Only runs if seeding is enabled in the configuration.
     *
     * @param args Command line arguments (not used)
     */
    @Override
    public void run(String... args) {
        if (!seeding) {
            log.info("Seeding is disabled. Skipping espaces data seeding.");
            return;
        }

        try {
            Path resourcePath = Paths.get(INIT_DATA_PATH);
            if (!Files.exists(resourcePath)) {
                log.warn("Resource path {} does not exist. Skipping espace data seeding.", resourcePath);
                return;
            }

            processEpaceJsonFile(resourcePath.toFile());
            log.info("Espace data seeding completed successfully.");
        } catch (Exception e) {
            log.error("Error during domain data seeding", e);
        }
    }

    /**
     * Processes a single domain folder, creating the domain and its sub-domains.
     *
     * @param folder The domain folder to process
     */
    private void processEpaceJsonFile(File folder) {
        File[] jsonFiles = folder
                .listFiles((dir, name) -> name.toLowerCase().endsWith(".json") && new File(dir, name).isFile());

        if (jsonFiles != null && jsonFiles.length > 0) {
            log.info("Found {} JSON files in folder: {}", jsonFiles.length, folder.getAbsolutePath());

            for (File file : jsonFiles) {
                try {
                    createEspacesFromJsonFile(file);

                } catch (Exception e) {
                    log.error("Failed to process file {}: {}", file.getName(), e.getMessage(), e);
                }
            }
        } else {
            log.debug("No JSON files found in folder: {}", folder.getAbsolutePath());
        }
    }

    @Transactional
    private void createEspacesFromJsonFile(File jsonFile) {

        try (InputStream inputStream = Files.newInputStream(jsonFile.toPath())) {
            log.info("Processing espaces file: {}", jsonFile.getName());
            List<EspaceDto> espaceList = objectMapper.readValue(inputStream,
                    new TypeReference<List<EspaceDto>>() {
                    });
            if (espaceList == null || espaceList.isEmpty()) {
                log.warn("No espaces found in file: {}", jsonFile.getName());
                return;
            }
            for (EspaceDto espace : espaceList) {
                try {

                    createEspace(espace);

                } catch (Exception e) {

                    log.error("Error creating espace {}: {}",
                            espace != null ? espace.getNom() : "unknown", e.getMessage(), e);
                }
            }

        } catch (Exception e) {
            log.error("Error processing espaces file {}: {}", jsonFile.getName(), e.getMessage());
        }
    }

    private void createEspace(EspaceDto espace) throws IOException {
        EspaceRequestDto requestDto = new EspaceRequestDto();
        requestDto.setNom(espace.getNom());
        requestDto.setDescription(espace.getDescription());
        requestDto.setApropos(espace.getApropos());
        // requestDto.setRole(espace.getRoleAcces().get(0).getRoleCode());
        requestDto.setStatut(espace.getStatut());
        if (StringUtils.hasText(espace.getImageUrl())) {
            // Fallback to the main directory if not found in images subdirectory
            Path imagePath = Paths.get(INIT_DATA_PATH, espace.getImageUrl());
            MultipartFile imageFile = FileToMultipartFileConverter.toMultipartFile(imagePath.toFile());

            requestDto.setImageFile(imageFile);

        }

        // Save the espace which will cascade the relationships
        Espace createdEspace = espaceService.create(requestDto);

        // Handle multiple role accesses
        if (espace.getRoleAcces() != null && !espace.getRoleAcces().isEmpty()) {
            espace.getRoleAcces().forEach(roleAcces -> {
                if (!roleAccesService.hasAccess(
                        roleAcces.getRoleCode(), "espace", createdEspace.getId(),
                        roleAcces.getNiveauAcces())) {
                    roleAccesService.addAccess(
                            roleAcces.getRoleCode(), "espace", createdEspace.getId(),
                            roleAcces.getNiveauAcces(), "system");
                }
            });
        }

        List<Domaine> domaines = domaineService.findAll();
        domaines.forEach(domaine -> {
            espaceService.attachDomaine(createdEspace.getId(), domaine.getId());
        });

    }

}