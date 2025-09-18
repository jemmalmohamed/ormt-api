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
import ma.org.ormt.modules.dashboard.tableaubord.dtos.TableauBordDto;
import ma.org.ormt.modules.dashboard.tableaubord.dtos.request.TableauBordRequestDto;
import ma.org.ormt.modules.dashboard.tableaubord.models.TableauBord;
import ma.org.ormt.modules.dashboard.tableaubord.services.TableauBordService;
import ma.org.ormt.modules.roleacces.services.RoleAccesService;
import ma.org.ormt.modules.roleacces.utils.RoleAccesMappingUtil;

@Log4j2
@Component
@Order(4)
@RequiredArgsConstructor
public class TableauBordSeeder implements CommandLineRunner {

    @Value("${starter.database.seed}")
    private boolean seeding;

    @Value("${data.external.data_path}")
    private String dataExternalPath;

    @Value("${data.external.territoire}")
    private String territoire;

    private final TableauBordService tableauBordService;

    private final FileDataService fileDataService;

    private final RoleAccesService roleAccesService;

    private static final String TABLEAUX_BORD_JSON_FILE = "tableaux_bord.json";

    /**
     * Executes the domain data seeding process when the application starts.
     * Only runs if seeding is enabled in the configuration.
     *
     * @param args Command line arguments (not used)
     */
    @Override
    public void run(String... args) {
        if (!seeding) {
            log.info("Seeding is disabled. Skipping tableaux_bord data seeding.");
            return;
        }

        try {
            String initDataPath = dataExternalPath + "/init-data/tableau_bord" + "/" + territoire;
            log.info("Using data path: {}", initDataPath);

            Path resourcePath = Paths.get(initDataPath);
            if (!Files.exists(resourcePath)) {
                log.warn("Resource path {} does not exist. Skipping tableau_bord data seeding.", resourcePath);
                return;
            }

            File jsonFile = new File(resourcePath.toFile(), TABLEAUX_BORD_JSON_FILE);
            if (!fileDataService.fileExists(jsonFile)) {
                log.warn("Tableaux_bord JSON file not found at: {}", jsonFile.getAbsolutePath());
                return;
            }

            createTableauxBordFromJsonFile(jsonFile);
            log.info("Tableaux_bord data seeding completed successfully.");
        } catch (Exception e) {
            log.error("Error during tableau_bord data seeding: {}", e.getMessage(), e);
        }
    }

    /**
     * Creates tableaux de bord from a JSON file.
     *
     * @param jsonFile The JSON file containing tableau_bord data
     */
    @Transactional
    private void createTableauxBordFromJsonFile(File jsonFile) {
        try {
            List<TableauBordDto> tbList = fileDataService.readJsonFileAsList(jsonFile,
                    new TypeReference<List<TableauBordDto>>() {
                    });
            if (tbList == null || tbList.isEmpty()) {
                log.warn("No tableaux_bord found in file: {}", jsonFile.getName());
                return;
            }
            for (TableauBordDto tb : tbList) {
                try {
                    createTableauBord(tb);
                } catch (ma.org.ormt.core.exceptions.handlers.ObjectsValidationException ove) {
                    // Log detailed validation errors
                    log.error("Validation failed for tableau_bord {}: {}", tb != null ? tb.getNom() : "unknown",
                            ove.getErrors());
                } catch (Exception e) {
                    log.error("Error creating tableau_bord {}: {}",
                            tb != null ? tb.getNom() : "unknown", e.getMessage(), e);
                }
            }

        } catch (Exception e) {
            log.error("Error processing tableaux_bord file {}: {}", jsonFile.getName(), e.getMessage());
        }
    }

    /**
     * Creates a single tableau de bord in the database.
     */
    private void createTableauBord(TableauBordDto tbDto) throws Exception {
        TableauBordRequestDto requestDto = new TableauBordRequestDto();
        requestDto.setNom(tbDto.getNom().toLowerCase());
        requestDto.setDescription(tbDto.getDescription().toLowerCase());
        requestDto.setActif(tbDto.getActif());

        // Skip creation if a tableau de bord with the same name already exists
        try {
            if (tableauBordService.findByNom(requestDto.getNom()).isPresent()) {
                log.info("Tableau_bord '{}' already exists. Skipping.", requestDto.getNom());
                return;
            }
        } catch (Exception ex) {
            // In case repository layer is not yet available, fall back to create and let
            // validation/DB handle it
            log.debug("Could not check existence for '{}': {}", requestDto.getNom(), ex.getMessage());
        }

        TableauBord created = tableauBordService.create(requestDto);
        // Handle role accesses generically for the created resource
        RoleAccesMappingUtil.applyRoleAccesses(roleAccesService, tbDto.getRoleAcces(),
                "tableauBord",
                created.getId(),
                ra -> ra.getRoleCode(),
                ra -> ra.getNiveauAcces(),
                "lecture");

        log.info("Created tableau_bord: {} (id={})", created.getNom(), created.getId());
    }

}