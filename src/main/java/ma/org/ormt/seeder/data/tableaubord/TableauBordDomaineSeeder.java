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
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.models.TBDomaine;
import ma.org.ormt.modules.dashboard.domaine.tbdomaine.services.TBDomaineService;
import ma.org.ormt.modules.dashboard.tableaubord.association.domaine.dtos.request.TableauBordDomaineRequestDto;
import ma.org.ormt.modules.dashboard.tableaubord.association.domaine.service.TableauBordDomaineService;
import ma.org.ormt.modules.dashboard.tableaubord.models.TableauBord;
import ma.org.ormt.modules.dashboard.tableaubord.services.TableauBordService;

@Log4j2
@Component
@Order(6)
@RequiredArgsConstructor
public class TableauBordDomaineSeeder implements CommandLineRunner {

    @Value("${starter.database.seed}")
    private boolean seeding;

    @Value("${data.external.data_path}")
    private String dataExternalPath;

    private final TableauBordDomaineService tableauBordDomaineService;
    private final TableauBordService tableauBordService;
    private final TBDomaineService tbDomaineService;
    private final FileDataService fileDataService;

    private static final String TABLEAU_BORD_DOMAINES_JSON_FILE = "tableau_bord_domaines.json";

    /**
     * Executes the tableau bord domaine association data seeding process when the
     * application starts.
     * Only runs if seeding is enabled in the configuration.
     *
     * @param args Command line arguments (not used)
     */
    @Override
    public void run(String... args) {
        if (!seeding) {
            log.info("Seeding is disabled. Skipping tableau bord domaines association data seeding.");
            return;
        }

        try {
            String initDataPath = dataExternalPath + "/init-data/tableau_bord";
            log.info("Using data path: {}", initDataPath);

            Path resourcePath = Paths.get(initDataPath);
            if (!Files.exists(resourcePath)) {
                log.warn("Resource path {} does not exist. Skipping tableau bord domaines association data seeding.",
                        resourcePath);
                return;
            }

            File jsonFile = new File(resourcePath.toFile(), TABLEAU_BORD_DOMAINES_JSON_FILE);
            if (!fileDataService.fileExists(jsonFile)) {
                log.warn("Tableau bord domaines association JSON file not found at: {}", jsonFile.getAbsolutePath());
                return;
            }

            createTableauBordDomainesFromJsonFile(jsonFile);
            log.info("Tableau bord domaines association data seeding completed successfully.");
        } catch (Exception e) {
            log.error("Error during tableau bord domaines association data seeding: {}", e.getMessage(), e);
        }
    }

    /**
     * Creates tableau bord domaine associations from a JSON file.
     *
     * @param jsonFile The JSON file containing tableau bord domaine association
     *                 data
     */
    @Transactional
    private void createTableauBordDomainesFromJsonFile(File jsonFile) {
        try {
            List<TableauBordDomaineRequestDto> associationList = fileDataService.readJsonFileAsList(jsonFile,
                    new TypeReference<List<TableauBordDomaineRequestDto>>() {
                    });

            if (associationList == null || associationList.isEmpty()) {
                log.warn("No tableau bord domaine associations found in file: {}", jsonFile.getName());
                return;
            }

            // Process associations in batches to improve performance
            for (TableauBordDomaineRequestDto association : associationList) {
                try {
                    createTableauBordDomaineAssociation(association);
                } catch (Exception e) {
                    log.error("Error creating tableau bord domaine association: {}", e.getMessage(), e);
                }
            }

        } catch (Exception e) {
            log.error("Error processing tableau bord domaines association file {}: {}", jsonFile.getName(),
                    e.getMessage());
        }
    }

    /**
     * Creates a single tableau bord domaine association in the database.
     */
    private void createTableauBordDomaineAssociation(TableauBordDomaineRequestDto requestDto) throws Exception {
        try {
            // Find TableauBord by name
            TableauBord tableauBord = tableauBordService.findByNom(requestDto.getTableauBord().getNom())
                    .orElseThrow(() -> new RuntimeException(
                            "TableauBord not found: " + requestDto.getTableauBord().getNom()));

            // Find TBDomaine by name
            TBDomaine tbDomaine = tbDomaineService.findByNom(requestDto.getTbDomaine().getNom())
                    .orElseThrow(
                            () -> new RuntimeException("TBDomaine not found: " + requestDto.getTbDomaine().getNom()));

            // Set the found entities in the request DTO
            requestDto.getTableauBord().setId(tableauBord.getId());
            requestDto.getTbDomaine().setId(tbDomaine.getId());

            // Check if association already exists
            List<TableauBordDomaineRequestDto> existingAssociations = List.of(requestDto);

            // Create the association
            tableauBordDomaineService.attachDomainesToTableauBord(existingAssociations);

            log.info("Created tableau bord domaine association: {} -> {} (ordre: {})",
                    tableauBord.getNom(), tbDomaine.getNom(), requestDto.getOrdre());

        } catch (Exception e) {
            log.error("Failed to create association between TableauBord '{}' and TBDomaine '{}': {}",
                    requestDto.getTableauBord() != null ? requestDto.getTableauBord().getNom() : "null",
                    requestDto.getTbDomaine() != null ? requestDto.getTbDomaine().getNom() : "null",
                    e.getMessage());
            throw e;
        }
    }
}
