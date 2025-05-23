package ma.org.ormt.seeder.data.espaces;

import java.io.File;
import java.io.IOException;
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

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ma.org.ormt.core.utilities.files.FileDataService;
import ma.org.ormt.core.utilities.files.FileToMultipartFileConverter;
import ma.org.ormt.modules.domaines.domaine.models.Domaine;
import ma.org.ormt.modules.domaines.domaine.services.DomaineService;
import ma.org.ormt.modules.espaces.dtos.EspaceDto;
import ma.org.ormt.modules.espaces.dtos.request.EspaceRequestDto;
import ma.org.ormt.modules.espaces.models.Espace;
import ma.org.ormt.modules.espaces.services.EspaceService;
import ma.org.ormt.modules.users.roleacces.services.RoleAccesService;

@Log4j2
@Component
@Order(4)
@RequiredArgsConstructor
public class EspaceSeeder implements CommandLineRunner {

    @Value("${starter.database.seed}")
    private boolean seeding;

    @Value("${data.external.data_path}")
    private String dataExternalPath;

    private final EspaceService espaceService;

    private final DomaineService domaineService;

    private final RoleAccesService roleAccesService;

    private final FileDataService fileDataService;

    private static final String ESPACES_JSON_FILE = "espaces.json";

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
            String initDataPath = dataExternalPath + "/init-data/espaces";
            log.info("Using data path: {}", initDataPath);

            Path resourcePath = Paths.get(initDataPath);
            if (!Files.exists(resourcePath)) {
                log.warn("Resource path {} does not exist. Skipping espace data seeding.", resourcePath);
                return;
            }

            File jsonFile = new File(resourcePath.toFile(), ESPACES_JSON_FILE);
            if (!fileDataService.fileExists(jsonFile)) {
                log.warn("Espaces JSON file not found at: {}", jsonFile.getAbsolutePath());
                return;
            }

            createEspacesFromJsonFile(jsonFile, initDataPath);
            log.info("Espace data seeding completed successfully.");
        } catch (Exception e) {
            log.error("Error during espace data seeding: {}", e.getMessage(), e);
        }
    }

    /**
     * Creates spaces from a JSON file.
     *
     * @param jsonFile     The JSON file containing espace data
     * @param initDataPath The base path for initialization data
     */
    @Transactional
    private void createEspacesFromJsonFile(File jsonFile, String initDataPath) {
        try {
            List<EspaceDto> espaceList = fileDataService.readJsonFileAsList(jsonFile,
                    new TypeReference<List<EspaceDto>>() {
                    });
            if (espaceList == null || espaceList.isEmpty()) {
                log.warn("No espaces found in file: {}", jsonFile.getName());
                return;
            }
            for (EspaceDto espace : espaceList) {
                try {
                    createEspace(espace, initDataPath);
                } catch (Exception e) {
                    log.error("Error creating espace {}: {}",
                            espace != null ? espace.getNom() : "unknown", e.getMessage(), e);
                }
            }

        } catch (Exception e) {
            log.error("Error processing espaces file {}: {}", jsonFile.getName(), e.getMessage());
        }
    }

    /**
     * Creates a single espace in the database.
     *
     * @param espace       The espace data from JSON
     * @param initDataPath The base path for initialization data
     * @throws Exception
     */
    private void createEspace(EspaceDto espace, String initDataPath) throws Exception {
        EspaceRequestDto requestDto = new EspaceRequestDto();
        requestDto.setNom(espace.getNom());
        requestDto.setDescription(espace.getDescription());
        requestDto.setApropos(espace.getApropos());
        requestDto.setActif(espace.getActif());

        handleEspaceImage(espace, requestDto, initDataPath);

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

    /**
     * Handles the image processing for an espace.
     *
     * @param espace       The espace data from JSON
     * @param requestDto   The request DTO to populate
     * @param initDataPath The base path for initialization data
     * @throws IOException
     */
    private void handleEspaceImage(EspaceDto espace, EspaceRequestDto requestDto, String initDataPath)
            throws IOException {
        if (StringUtils.hasText(espace.getImageUrl())) {
            Path imagePath = Paths.get(initDataPath, espace.getImageUrl());
            if (!Files.exists(imagePath)) {
                log.error("Image not found at path: {}. Trying direct path.", imagePath);
                // Fallback to the main directory if not found in images subdirectory
                imagePath = Paths.get(initDataPath, espace.getImageUrl());
            }

            if (Files.exists(imagePath)) {
                MultipartFile imageFile = FileToMultipartFileConverter.toMultipartFile(imagePath.toFile());
                requestDto.setImageFile(imageFile);
                log.info("Found image for espace '{}' at: {}", espace.getNom(), imagePath);
            } else {
                log.error("Image for espace '{}' not found at: {}", espace.getNom(), imagePath);
            }
        }
    }
}