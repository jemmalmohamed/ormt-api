package ma.org.ormt.seeder.data.tableaubord;

import java.io.File;
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

import com.fasterxml.jackson.core.type.TypeReference;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ma.org.ormt.core.utilities.files.FileDataService;
import ma.org.ormt.modules.dashboard.tbd.models.TbdDashboard;
import ma.org.ormt.modules.dashboard.tbd.models.TbdSection;
import ma.org.ormt.modules.dashboard.tbd.models.TbdSourceListing;
import ma.org.ormt.modules.dashboard.tbd.models.TbdWidget;
import ma.org.ormt.modules.dashboard.tbd.models.TbdWidgetRow;
import ma.org.ormt.modules.dashboard.tbd.repositories.TbdDashboardRepository;
import ma.org.ormt.modules.dashboard.tbd.repositories.TbdSectionRepository;
import ma.org.ormt.modules.dashboard.tbd.repositories.TbdSourceListingRepository;
import ma.org.ormt.modules.dashboard.tbd.repositories.TbdWidgetRepository;
import ma.org.ormt.modules.dashboard.tbd.repositories.TbdWidgetRowRepository;
import ma.org.ormt.modules.indicateurs.source.models.Source;
import ma.org.ormt.modules.indicateurs.source.repositories.SourceRepository;

@Log4j2
@Component
@Order(8)
@RequiredArgsConstructor
public class TbdDashboardJsonSeeder implements CommandLineRunner {

    private static final String TDB_DASHBOARDS_JSON_FILE = "tbd_dashboards.json";

    @Value("${starter.database.seed}")
    private boolean seeding;

    @Value("${data.external.data_path}")
    private String dataExternalPath;

    @Value("${data.external.territoire}")
    private String territoire;

    private final FileDataService fileDataService;
    private final TbdDashboardRepository dashboardRepository;
    private final TbdSourceListingRepository sourceListingRepository;
    private final TbdSectionRepository sectionRepository;
    private final TbdWidgetRowRepository widgetRowRepository;
    private final TbdWidgetRepository widgetRepository;
    private final SourceRepository sourceRepository;

    @Override
    public void run(String... args) {
        if (!seeding) {
            log.info("Seeding is disabled. Skipping TDB dashboard JSON seeding.");
            return;
        }

        try {
            String initDataPath = dataExternalPath + "/init-data/tableau_bord/" + territoire;
            Path resourcePath = Paths.get(initDataPath);
            if (!Files.exists(resourcePath)) {
                log.warn("Resource path {} does not exist. Skipping TDB dashboard JSON seeding.", resourcePath);
                return;
            }

            File jsonFile = new File(resourcePath.toFile(), TDB_DASHBOARDS_JSON_FILE);
            if (!fileDataService.fileExists(jsonFile)) {
                log.warn("TDB dashboards JSON file not found at: {}", jsonFile.getAbsolutePath());
                return;
            }

            createDashboardsFromJsonFile(jsonFile);
            log.info("TDB dashboard JSON seeding completed successfully.");
        } catch (Exception e) {
            log.error("Error during TDB dashboard JSON seeding: {}", e.getMessage(), e);
        }
    }

    @Transactional
    private void createDashboardsFromJsonFile(File jsonFile) {
        try {
            List<TbdDashboardSeed> dashboardSeeds = fileDataService.readJsonFileAsList(
                    jsonFile,
                    new TypeReference<List<TbdDashboardSeed>>() {
                    });

            if (dashboardSeeds == null || dashboardSeeds.isEmpty()) {
                log.warn("No TDB dashboards found in file: {}", jsonFile.getName());
                return;
            }

            for (TbdDashboardSeed dashboardSeed : dashboardSeeds) {
                try {
                    createDashboard(dashboardSeed);
                } catch (Exception e) {
                    log.error("Error creating TDB dashboard {}: {}",
                            dashboardSeed != null ? dashboardSeed.getNom() : "unknown", e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            log.error("Error processing TDB dashboards file {}: {}", jsonFile.getName(), e.getMessage(), e);
        }
    }

    private void createDashboard(TbdDashboardSeed seed) {
        if (seed == null || seed.getNom() == null || seed.getNom().isBlank()) {
            log.warn("Skipping invalid TDB dashboard seed without nom.");
            return;
        }

        Optional<TbdDashboard> existing = dashboardRepository.findByNomIgnoreCaseAndActifTrue(seed.getNom());
        if (existing.isPresent()) {
            log.info("TDB dashboard '{}' already exists. Skipping.", seed.getNom());
            return;
        }

        TbdDashboard dashboard = dashboardRepository.save(TbdDashboard.builder()
                .nom(seed.getNom())
                .titre(seed.getTitre())
                .sousTitre(seed.getSousTitre())
                .description(seed.getDescription())
                .sourceText(seed.getSourceText())
                .status(seed.getStatus() == null || seed.getStatus().isBlank() ? "DRAFT" : seed.getStatus())
                .actif(seed.getActif() == null ? true : seed.getActif())
                .build());

        createSources(dashboard.getId(), seed.getSources());
        createSections(dashboard.getId(), seed.getSections());
        log.info("Created TDB dashboard: {} (id={})", dashboard.getNom(), dashboard.getId());
    }

    private void createSources(Long dashboardId, List<TbdSourceSeed> sources) {
        if (sources == null || sources.isEmpty()) {
            return;
        }
        for (TbdSourceSeed sourceSeed : sources) {
            if (sourceSeed.getNom() == null || sourceSeed.getNom().isBlank()) {
                continue;
            }
            Optional<Source> sourceOpt = sourceRepository.findByNomIgnoreCase(sourceSeed.getNom());
            if (sourceOpt.isEmpty()) {
                log.warn("Source '{}' not found for TDB dashboard {}", sourceSeed.getNom(), dashboardId);
                continue;
            }
            sourceListingRepository.save(TbdSourceListing.builder()
                    .dashboardId(dashboardId)
                    .source(sourceOpt.get())
                    .ordre(sourceSeed.getOrdre() == null ? 0 : sourceSeed.getOrdre())
                    .build());
        }
    }

    private void createSections(Long dashboardId, List<TbdSectionSeed> sections) {
        if (sections == null || sections.isEmpty()) {
            return;
        }
        for (TbdSectionSeed sectionSeed : sections) {
            TbdSection section = sectionRepository.save(TbdSection.builder()
                    .dashboardId(dashboardId)
                    .label(sectionSeed.getLabel())
                    .ordre(sectionSeed.getOrdre() == null ? 0 : sectionSeed.getOrdre())
                    .sizePercent(sectionSeed.getSizePercent() == null ? 33 : sectionSeed.getSizePercent())
                    .actif(true)
                    .build());
            createRows(section.getId(), sectionSeed.getRows());
        }
    }

    private void createRows(Long sectionId, List<TbdRowSeed> rows) {
        if (rows == null || rows.isEmpty()) {
            return;
        }
        for (TbdRowSeed rowSeed : rows) {
            TbdWidgetRow row = widgetRowRepository.save(TbdWidgetRow.builder()
                    .sectionId(sectionId)
                    .ordre(rowSeed.getOrdre() == null ? 0 : rowSeed.getOrdre())
                    .sizePercent(rowSeed.getSizePercent() == null ? 100 : rowSeed.getSizePercent())
                    .heightPx(rowSeed.getHeightPx() == null ? 200 : rowSeed.getHeightPx())
                    .build());
            createWidgets(row.getId(), rowSeed.getWidgets());
        }
    }

    private void createWidgets(Long rowId, List<TbdWidgetSeed> widgets) {
        if (widgets == null || widgets.isEmpty()) {
            return;
        }
        for (TbdWidgetSeed widgetSeed : widgets) {
            widgetRepository.save(TbdWidget.builder()
                    .rowId(rowId)
                    .type(widgetSeed.getType())
                    .titre(widgetSeed.getTitre())
                    .ordre(widgetSeed.getOrdre() == null ? 0 : widgetSeed.getOrdre())
                    .sizePercent(widgetSeed.getSizePercent() == null ? 50 : widgetSeed.getSizePercent())
                    .contentJson(widgetSeed.getContentJson())
                    .kpiId(widgetSeed.getKpiId())
                    .actif(true)
                    .build());
        }
    }

    @Data
    private static class TbdDashboardSeed {
        private String nom;
        private String titre;
        private String sousTitre;
        private String description;
        private String sourceText;
        private String status;
        private Boolean actif;
        private List<TbdSourceSeed> sources;
        private List<TbdSectionSeed> sections;
    }

    @Data
    private static class TbdSourceSeed {
        private String nom;
        private Integer ordre;
    }

    @Data
    private static class TbdSectionSeed {
        private String label;
        private Integer ordre;
        private Integer sizePercent;
        private List<TbdRowSeed> rows;
    }

    @Data
    private static class TbdRowSeed {
        private Integer ordre;
        private Integer sizePercent;
        private Integer heightPx;
        private List<TbdWidgetSeed> widgets;
    }

    @Data
    private static class TbdWidgetSeed {
        private String type;
        private String titre;
        private Integer ordre;
        private Integer sizePercent;
        private Long kpiId;
        private String contentJson;
    }
}
