package ma.org.ormt.seeder.data.publications;

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
import ma.org.ormt.core.utilities.files.FileToMultipartFileConverter;
import ma.org.ormt.modules.publications.publication.dtos.PublicationDto;
import ma.org.ormt.modules.publications.publication.dtos.request.PublicationRequestDto;
import ma.org.ormt.modules.publications.publication.models.Publication;
import ma.org.ormt.modules.publications.publication.repositories.PublicationRepository;
import ma.org.ormt.modules.publications.publication.services.PublicationService;
import ma.org.ormt.modules.roleacces.services.RoleAccesService;
import ma.org.ormt.modules.roleacces.utils.RoleAccesMappingUtil;

@Log4j2
@Component
@Order(4)
@RequiredArgsConstructor
public class PublicationSeeder implements CommandLineRunner {

    @Value("${starter.database.seed}")
    private boolean seeding;

    @Value("${data.external.data_path}")
    private String dataExternalPath;

    private final PublicationRepository publicationRepository;
    private final PublicationService publicationService;
    private final ObjectMapper objectMapper;
    private final RoleAccesService roleAccesService;

    private static final String PUBLICATIONS_JSON_FILE = "publications.json";

    /**
     * Executes the publication data seeding process when the application starts.
     * Only runs if seeding is enabled in the configuration.
     *
     * @param args Command line arguments (not used)
     */
    @Override
    public void run(String... args) {
        if (!seeding) {
            log.info("Seeding is disabled. Skipping publication data seeding.");
            return;
        }

        try {
            String initDataPath = dataExternalPath + "/init-data/publications";
            log.info("Using data path: {}", initDataPath);

            Path resourcePath = Paths.get(initDataPath);
            if (!Files.exists(resourcePath)) {
                log.warn("Resource path {} does not exist. Skipping publication data seeding.", resourcePath);
                return;
            }

            File jsonFile = new File(resourcePath.toFile(), PUBLICATIONS_JSON_FILE);
            if (!jsonFile.exists() || !jsonFile.isFile()) {
                log.warn("Publications JSON file not found at: {}", jsonFile.getAbsolutePath());
                return;
            }

            createPublicationsFromJsonFile(jsonFile, initDataPath);
            log.info("Publication data seeding completed successfully.");
        } catch (Exception e) {
            log.error("Error during publication data seeding: {}", e.getMessage(), e);
        }
    }

    /**
     * Creates publications from a JSON file.
     *
     * @param jsonFile     The JSON file containing publication data
     * @param initDataPath The base path for initialization data
     */
    @Transactional
    private void createPublicationsFromJsonFile(File jsonFile, String initDataPath) {
        try (InputStream inputStream = Files.newInputStream(jsonFile.toPath())) {
            log.info("Processing publications file: {}", jsonFile.getName());
            List<PublicationDto> publicationList = objectMapper.readValue(
                    inputStream,
                    new TypeReference<List<PublicationDto>>() {
                    });

            if (publicationList == null || publicationList.isEmpty()) {
                log.warn("No publications found in file: {}", jsonFile.getName());
                return;
            }

            for (PublicationDto publication : publicationList) {
                try {
                    createPublication(publication, initDataPath);
                } catch (Exception e) {
                    log.error("Error creating publication '{}': {}",
                            publication != null ? publication.getTitre() : "unknown", e.getMessage(), e);
                }
            }

        } catch (IOException e) {
            log.error("Error reading publications file {}: {}", jsonFile.getName(), e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error processing publications file {}: {}", jsonFile.getName(), e.getMessage(), e);
        }
    }

    /**
     * Creates a single publication in the database.
     *
     * @param publicationDto The publication data from JSON
     * @throws Exception
     */
    private void createPublication(PublicationDto publicationDto, String initDataPath) throws Exception {
        if (publicationDto == null || !StringUtils.hasText(publicationDto.getTitre())) {
            log.warn("Skipping invalid publication data: missing titre");
            return;
        }

        // Check if publication already exists
        Optional<Publication> existingPublication = publicationRepository.findByTitre(publicationDto.getTitre());
        if (existingPublication.isPresent()) {
            log.info("Publication with titre '{}' already exists. Skipping.", publicationDto.getTitre());
            return;
        }
        // Create a DTO from the publication entity
        PublicationRequestDto publicationRequestDto = new PublicationRequestDto();
        publicationRequestDto.setTitre(publicationDto.getTitre());
        publicationRequestDto.setDescription(publicationDto.getDescription());
        publicationRequestDto.setDatePublication(publicationDto.getDatePublication());
        publicationRequestDto.setAuteur(publicationDto.getAuteur());
        publicationRequestDto.setActif(true);
        publicationRequestDto.setCategorie(publicationDto.getCategorie());
        publicationRequestDto.setTags(publicationDto.getTags());
        publicationRequestDto.setNombreTelechargements(publicationDto.getNombreTelechargements());

        // Process the image only if a imageUrl is provided
        if (StringUtils.hasText(publicationDto.getFichierUrl())) {
            // Look for the image in the images subfolder
            Path imagePath = Paths.get(initDataPath, publicationDto.getFichierUrl());
            if (!Files.exists(imagePath)) {
                log.error("Image not found at path: {}. Trying direct path.", imagePath);
                // Fallback to the main directory if not found in images subdirectory
                imagePath = Paths.get(initDataPath, publicationDto.getFichierUrl());
            }

            if (Files.exists(imagePath)) {
                MultipartFile imageFile = FileToMultipartFileConverter.toMultipartFile(imagePath.toFile());
                publicationRequestDto.setFichier(imageFile);
                log.info("Found image for publication '{}' at: {}", publicationDto.getTitre(), imagePath);
            } else {
                log.error("Image for publication '{}' not found at: {}", publicationDto.getTitre(), imagePath);
            }
        }

        Publication createdPublication = publicationService.create(publicationRequestDto);

        // Handle role accesses generically for the created resource
        RoleAccesMappingUtil.applyRoleAccesses(roleAccesService, publicationDto.getRoleAcces(),
                "publication",
                createdPublication.getId(),
                ra -> ra.getRoleCode(),
                ra -> ra.getNiveauAcces(),
                "lecture");

        log.info("Created publication: {}", publicationDto.getTitre());
    }
}