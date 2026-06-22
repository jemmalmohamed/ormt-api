package ma.org.ormt.seeder.data.tbgroup;

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
import ma.org.ormt.modules.dashboard.tbgroup.dtos.TbGroupDto;
import ma.org.ormt.modules.dashboard.tbgroup.dtos.request.TbGroupRequestDto;
import ma.org.ormt.modules.dashboard.tbgroup.models.TbGroup;
import ma.org.ormt.modules.dashboard.tbgroup.services.TbGroupService;
import ma.org.ormt.modules.roleacces.services.RoleAccesService;
import ma.org.ormt.modules.roleacces.utils.RoleAccesMappingUtil;

@Log4j2
@Component
@Order(4)
@RequiredArgsConstructor
public class TbGroupSeeder implements CommandLineRunner {

    @Value("${starter.database.seed}")
    private boolean seeding;

    @Value("${data.external.data_path}")
    private String dataExternalPath;

    @Value("${data.external.territoire}")
    private String territoire;

    private final TbGroupService tbGroupService;

    private final FileDataService fileDataService;

    private final RoleAccesService roleAccesService;

    private static final String TABLEAUX_BORD_JSON_FILE = "tb_groups.json";

    /**
     * Executes the domain data seeding process when the application starts.
     * Only runs if seeding is enabled in the configuration.
     *
     * @param args Command line arguments (not used)
     */
    @Override
    public void run(String... args) {
        if (!seeding) {
            log.info("Seeding is disabled. Skipping tb_groups data seeding.");
            return;
        }

        try {
            String initDataPath = dataExternalPath + "/init-data/tb_group" + "/" + territoire;
            log.info("Using data path: {}", initDataPath);

            Path resourcePath = Paths.get(initDataPath);
            if (!Files.exists(resourcePath)) {
                log.warn("Resource path {} does not exist. Skipping tb_group data seeding.", resourcePath);
                return;
            }

            File jsonFile = new File(resourcePath.toFile(), TABLEAUX_BORD_JSON_FILE);
            if (!fileDataService.fileExists(jsonFile)) {
                log.warn("TB groups JSON file not found at: {}", jsonFile.getAbsolutePath());
                return;
            }

            createTableauxBordFromJsonFile(jsonFile);
            log.info("TB groups data seeding completed successfully.");
        } catch (Exception e) {
            log.error("Error during tb_group data seeding: {}", e.getMessage(), e);
        }
    }

    /**
     * Creates tableaux de bord from a JSON file.
     *
     * @param jsonFile The JSON file containing tb_group data
     */
    @Transactional
    private void createTableauxBordFromJsonFile(File jsonFile) {
        try {
            List<TbGroupDto> tbList = fileDataService.readJsonFileAsList(jsonFile,
                    new TypeReference<List<TbGroupDto>>() {
                    });
            if (tbList == null || tbList.isEmpty()) {
                log.warn("No tb_groups found in file: {}", jsonFile.getName());
                return;
            }
            for (TbGroupDto tb : tbList) {
                try {
                    createTbGroup(tb);
                } catch (ma.org.ormt.core.exceptions.handlers.ObjectsValidationException ove) {
                    // Log detailed validation errors
                    log.error("Validation failed for tb_group {}: {}", tb != null ? tb.getNom() : "unknown",
                            ove.getErrors());
                } catch (Exception e) {
                    log.error("Error creating tb_group {}: {}",
                            tb != null ? tb.getNom() : "unknown", e.getMessage(), e);
                }
            }

        } catch (Exception e) {
            log.error("Error processing tb_groups file {}: {}", jsonFile.getName(), e.getMessage());
        }
    }

    /**
     * Creates a single tableau de bord in the database.
     */
    private void createTbGroup(TbGroupDto tbDto) throws Exception {
        TbGroupRequestDto requestDto = new TbGroupRequestDto();
        requestDto.setNom(tbDto.getNom().toLowerCase());
        requestDto.setDescription(tbDto.getDescription().toLowerCase());
        requestDto.setActif(tbDto.getActif());

        // Skip creation if a tableau de bord with the same name already exists
        try {
            if (tbGroupService.findByNom(requestDto.getNom()).isPresent()) {
                log.info("Tableau_bord '{}' already exists. Skipping.", requestDto.getNom());
                return;
            }
        } catch (Exception ex) {
            // In case repository layer is not yet available, fall back to create and let
            // validation/DB handle it
            log.debug("Could not check existence for '{}': {}", requestDto.getNom(), ex.getMessage());
        }

        TbGroup created = tbGroupService.create(requestDto);
        // Handle role accesses generically for the created resource
        RoleAccesMappingUtil.applyRoleAccesses(roleAccesService, tbDto.getRoleAcces(),
                "tbGroup",
                created.getId(),
                ra -> ra.getRoleCode(),
                ra -> ra.getNiveauAcces(),
                "lecture");

        log.info("Created tb_group: {} (id={})", created.getNom(), created.getId());
    }

}
