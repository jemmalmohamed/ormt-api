package ma.org.ancfcc.pva.seeder.data.mission;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ma.org.ancfcc.pva.core.gis.shapefile.ShpFileService;
import ma.org.ancfcc.pva.core.threads.ThreadService;
import ma.org.ancfcc.pva.core.utilities.FileUtils;
import ma.org.ancfcc.pva.modules.mission.service.imports.MissionImportService;

@Log4j2
@Component
@RequiredArgsConstructor
@Order(10)
public class MissionLevelOneSeeder implements CommandLineRunner {

    private static final Integer WGS_SRID = 4326;

    @Value("${starter.database.seed}")
    private boolean seeding;

    @Value("${data.external.level_1_path}")
    private String levelOnePath;

    private final MissionImportService missionImportService;

    @Override
    public void run(String... args) throws Exception {

        if (!seeding) {
            log.info("### DATA: Skipping data seeding");
            return;
        }

        List<String> yearFolderList = Arrays.asList("2023");
        // List<String> yearFolderList = Arrays.asList(
        // "2013",
        // "2014",
        // "2015",
        // "2016",
        // "2017",
        // "2018",
        // "2019",
        // "2020",
        // "2021",
        // "2022",
        // "2023",
        // "2024");

        log.info("### DATA: Début chargement données mission format shapefiles...");
        processFolderList(yearFolderList);
        log.info("### DATA: Fin chargement données mission format shapefiles...");
    }

    public void processFolderList(List<String> yearFolderList) {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        try {
            List<Future<?>> futures = new ArrayList<>();
            for (String yearFolder : yearFolderList) {
                futures.add(
                        executor.submit(() -> processYearFolder(levelOnePath + yearFolder)));
            }
            ThreadService.waitForCompletion(futures);
        } finally {
            ThreadService.shutdownExecutorService(executor);
        }
    }

    private void processYearFolder(String yearFolder) {
        try {
            File resourceDir = new File(yearFolder);
            FileUtils.validateDirectory(resourceDir);
            File[] filesArray = resourceDir.listFiles();
            List<File> shapefileComponents = Arrays.asList(filesArray);
            ShpFileService.validateShapefileComponents(shapefileComponents);
            missionImportService.importMissionFromShapefile(shapefileComponents, WGS_SRID);

        } catch (IOException e) {
            log.error("### DATA: Error processing folder {}: {}", yearFolder, e.getMessage());
        }
    }

}
