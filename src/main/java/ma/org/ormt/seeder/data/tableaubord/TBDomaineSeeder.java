package ma.org.ormt.seeder.data.tableaubord;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ma.org.ormt.core.utilities.files.FileDataService;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.dtos.TBDomaineDto;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.dtos.request.TBDomaineRequestDto;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.models.TBDomaine;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.services.TBDomaineService;
import ma.org.ormt.modules.roleacces.services.RoleAccesService;
import ma.org.ormt.modules.roleacces.utils.RoleAccesMappingUtil;

@Log4j2
@Component
@Order(5)
@RequiredArgsConstructor
public class TBDomaineSeeder implements CommandLineRunner {

    @Value("${starter.database.seed}")
    private boolean seeding;

    @Value("${data.external.data_path}")
    private String dataExternalPath;

    @Value("${data.external.territoire}")
    private String territoire;

    private final TBDomaineService tbDomaineService;

    private final FileDataService fileDataService;

    private final RoleAccesService roleAccesService;

    private static final String TB_DOMAINES_JSON_FILE = "tb_domaines.json";

    /**
     * Executes the TB domain data seeding process when the application starts.
     * Only runs if seeding is enabled in the configuration.
     *
     * @param args Command line arguments (not used)
     */
    @Override
    public void run(String... args) {
        if (!seeding) {
            log.info("Seeding is disabled. Skipping TB domaines data seeding.");
            return;
        }

        try {
            String initDataPath = dataExternalPath + "/init-data/tableau_bord" + "/" + territoire;
            log.info("Using data path: {}", initDataPath);

            Path resourcePath = Paths.get(initDataPath);
            if (!Files.exists(resourcePath)) {
                log.warn("Resource path {} does not exist. Skipping TB domaines data seeding.", resourcePath);
                return;
            }

            File jsonFile = new File(resourcePath.toFile(), TB_DOMAINES_JSON_FILE);
            if (!fileDataService.fileExists(jsonFile)) {
                log.warn("TB domaines JSON file not found at: {}", jsonFile.getAbsolutePath());
                return;
            }

            createTBDomainesFromJsonFile(jsonFile);
            log.info("TB domaines data seeding completed successfully.");
        } catch (Exception e) {
            log.error("Error during TB domaines data seeding: {}", e.getMessage(), e);
        }
    }

    /**
     * Creates TB domaines from a JSON file.
     *
     * @param jsonFile The JSON file containing TB domaines data
     */
    @Transactional
    private void createTBDomainesFromJsonFile(File jsonFile) {
        try {
            List<TBDomaineDto> tbDomaineList = fileDataService.readJsonFileAsList(jsonFile,
                    new TypeReference<List<TBDomaineDto>>() {
                    });
            if (tbDomaineList == null || tbDomaineList.isEmpty()) {
                log.warn("No TB domaines found in file: {}", jsonFile.getName());
                return;
            }
            for (TBDomaineDto tbDomaine : tbDomaineList) {
                try {
                    createTBDomaine(tbDomaine);
                } catch (ma.org.ormt.core.exceptions.handlers.ObjectsValidationException ove) {
                    // Log detailed validation errors
                    log.error("Validation failed for TB domaine {}: {}",
                            tbDomaine != null ? tbDomaine.getNom() : "unknown", ove.getErrors());
                } catch (Exception e) {
                    log.error("Error creating TB domaine {}: {}",
                            tbDomaine != null ? tbDomaine.getNom() : "unknown", e.getMessage(), e);
                }
            }

        } catch (Exception e) {
            log.error("Error processing TB domaines file {}: {}", jsonFile.getName(), e.getMessage());
        }
    }

    /**
     * Creates a single TB domaine in the database.
     */
    private void createTBDomaine(TBDomaineDto tbDomaineDto) throws Exception {
        TBDomaineRequestDto requestDto = new TBDomaineRequestDto();
        requestDto.setNom(tbDomaineDto.getNom().toLowerCase());
        requestDto.setDescription(tbDomaineDto.getDescription());
        requestDto.setLibelle(tbDomaineDto.getLibelle().toLowerCase());
        requestDto.setActif(tbDomaineDto.getActif());

        // Skip creation if a TB domaine with the same name already exists
        try {
            if (tbDomaineService.findByNom(requestDto.getNom()).isPresent()) {
                log.info("TB domaine '{}' already exists. Skipping.", requestDto.getNom());
                return;
            }
        } catch (Exception ex) {
            // In case repository layer is not yet available, fall back to create and let
            // validation/DB handle it
            log.debug("Could not check existence for '{}': {}", requestDto.getNom(), ex.getMessage());
        }

        TBDomaine created = tbDomaineService.create(requestDto);

        // Handle role accesses generically for the created resource
        RoleAccesMappingUtil.applyRoleAccesses(roleAccesService, tbDomaineDto.getRoleAcces(),
                "tbDomaine",
                created.getId(),
                ra -> ra.getRoleCode(),
                ra -> ra.getNiveauAcces(),
                "lecture");

        log.info("Created TB domaine: {} (id={})", created.getNom(), created.getId());
    }
}
