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
import ma.org.ormt.modules.chiffres.dtos.ChiffreCleDto;
import ma.org.ormt.modules.chiffres.dtos.request.ChiffreCleRequestDto;
import ma.org.ormt.modules.chiffres.models.ChiffreCle;
import ma.org.ormt.modules.chiffres.services.ChiffreCleService;
import ma.org.ormt.modules.indicateurs.donnee.models.DonneeIndicateur;
import ma.org.ormt.modules.indicateurs.indicateur.association.dimension.models.IndicateurDimension;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;
import ma.org.ormt.modules.indicateurs.indicateur.services.indicateur.IndicateurService;
import ma.org.ormt.modules.roleacces.services.RoleAccesService;
import ma.org.ormt.modules.roleacces.utils.RoleAccesMappingUtil;

@Log4j2
@Component
@Order(6)
@RequiredArgsConstructor
public class ChiffreCleSeeder implements CommandLineRunner {

    @Value("${starter.database.seed}")
    private boolean seeding;

    @Value("${data.external.data_path}")
    private String dataExternalPath;

    private final ChiffreCleService chiffreCleService;

    private final IndicateurService indicateurService;

    private final RoleAccesService roleAccesService;

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

            createChiffreCleFromIndicateurDonnee("Nombre d'offre d'emploi recueillies");
            // createChiffreCleFromIndicateurDonnee(
            // "Emploi par secteur d'emploi");
            // createChiffreCleFromIndicateurDonnee("Emploi par âge");

            log.info("ChiffreCle data seeding completed successfully.");
        } catch (Exception e) {
            log.error("Error during chiffreCle data seeding: {}", e.getMessage(), e);
        }
    }

    @Transactional
    private void createChiffreCleFromIndicateurDonnee(String indicateurName) {
        Indicateur indicateur = indicateurService
                .findByNomWithDonneesAndDimensions(indicateurName)
                .orElse(null);
        if (indicateur == null) {
            log.warn("Indicateur not found: {}", indicateurName);
            return;
        }

        // Find the temporal dimension for this indicateur
        IndicateurDimension temporelleIndDim = indicateur.getIndicateurDimensions().stream()
                .filter(idim -> Boolean.TRUE.equals(idim.getTemporelle()))
                .findFirst()
                .orElse(null);
        if (temporelleIndDim == null) {
            log.warn("No temporal dimension found for Indicateur: {}", indicateur.getNom());
            return;
        }
        Long temporelleDimensionId = temporelleIndDim.getDimension().getId();
        // For each DonneeIndicateur, get the temporal value (e.g., year)
        DonneeIndicateur latestDonnee = indicateur.getDonnees().stream()
                .filter(donnee -> donnee.getValeurDimensions() != null)
                .filter(donnee -> donnee.getValeurDimensions().stream()
                        .anyMatch(vd -> vd.getDimension() != null
                                && vd.getDimension().getId().equals(temporelleDimensionId)))
                .max((d1, d2) -> {
                    String v1 = d1.getValeurDimensions().stream()
                            .filter(vd -> vd.getDimension() != null
                                    && vd.getDimension().getId().equals(temporelleDimensionId))
                            .map(vd -> vd.getValeur())
                            .findFirst().orElse("");
                    String v2 = d2.getValeurDimensions().stream()
                            .filter(vd -> vd.getDimension() != null
                                    && vd.getDimension().getId().equals(temporelleDimensionId))
                            .map(vd -> vd.getValeur())
                            .findFirst().orElse("");
                    // Try to compare as integer (e.g., year), fallback to string
                    try {
                        return Integer.compare(Integer.parseInt(v1), Integer.parseInt(v2));
                    } catch (NumberFormatException e) {
                        return v1.compareTo(v2);
                    }
                })
                .orElse(null);
        if (latestDonnee == null) {
            log.warn("No DonneeIndicateur with temporal value found for Indicateur: {}", indicateur.getNom());
            return;
        }
        // Create ChiffreCle from latestDonnee
        ChiffreCleRequestDto requestDto = new ChiffreCleRequestDto();
        requestDto.setLibelle(indicateur.getNom());
        requestDto.setDescription(indicateur.getDescription());
        requestDto.setValeur(latestDonnee.getValeur());
        requestDto.setUnite(indicateur.getUnite());
        requestDto.setAfficherDate(false);
        requestDto.setActif(Boolean.TRUE);
        try {
            chiffreCleService.create(requestDto);
            log.info("Created chiffreCle from DonneeIndicateur: {}", requestDto.getLibelle());
        } catch (Exception e) {
            log.error("Error creating chiffreCle from DonneeIndicateur: {}",
                    e.getMessage() + " for Indicateur: " + indicateur.getNom(), e);
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
            List<ChiffreCleDto> chiffreCleList = objectMapper.readValue(
                    inputStream,
                    new TypeReference<List<ChiffreCleDto>>() {
                    });

            if (chiffreCleList == null || chiffreCleList.isEmpty()) {
                log.warn("No chiffreCles found in file: {}", jsonFile.getName());
                return;
            }

            for (ChiffreCleDto chiffreCle : chiffreCleList) {
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
     * @param chiffreCleDto The partner data from JSON
     * @param initDataPath  The base path for initialization data
     * @throws IOException If there's an error processing the image file
     */
    private void createChiffreCle(ChiffreCleDto chiffreCleDto, String initDataPath) throws IOException {
        if (chiffreCleDto == null || !StringUtils.hasText(chiffreCleDto.getLibelle())) {
            log.warn("Skipping invalid chiffreCle data: missing name");
            return;
        }

        // Check if partner already exists
        Optional<ChiffreCle> existingChiffreCle = chiffreCleService.findByLibelle(chiffreCleDto.getLibelle());
        if (existingChiffreCle.isPresent()) {
            log.info("ChiffreCle with name '{}' already exists. Skipping.", chiffreCleDto.getLibelle());
            return;
        }

        ChiffreCleRequestDto requestDto = new ChiffreCleRequestDto();
        requestDto.setLibelle(chiffreCleDto.getLibelle());
        requestDto.setDescription(chiffreCleDto.getDescription());
        requestDto.setValeur(chiffreCleDto.getValeur());
        requestDto.setUnite(chiffreCleDto.getUnite());
        requestDto.setAccessType(chiffreCleDto.getAccessType());
        requestDto.setAfficherDate(chiffreCleDto.getAfficherDate());
        requestDto.setActif(chiffreCleDto.getActif());

        try {
            ChiffreCle createdChiffreCle = chiffreCleService.create(requestDto);
            // Handle role accesses generically for the created resource
            RoleAccesMappingUtil.applyRoleAccesses(roleAccesService, chiffreCleDto.getRoleAcces(),
                    "chiffreCle",
                    createdChiffreCle.getId(),
                    ra -> ra.getRoleCode(),
                    ra -> ra.getNiveauAcces(),
                    "lecture");

            log.info("Created chiffreCle: {}", chiffreCleDto.getLibelle());
        } catch (Exception e) {
            log.error("Error creating chiffreCle {}: {}", chiffreCleDto.getLibelle(), e.getMessage());
        }
    }
}