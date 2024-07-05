package ma.org.ancfcc.pva.seeder.data.mission;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;
import jakarta.xml.bind.JAXBException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ma.org.ancfcc.pva.core.gis.shapefile.ShpFileService;
import ma.org.ancfcc.pva.core.threads.ThreadService;
import ma.org.ancfcc.pva.core.utilities.FileUtils;
import ma.org.ancfcc.pva.modules.mission.models.Mission;
import ma.org.ancfcc.pva.modules.mission.service.MissionService;
import ma.org.ancfcc.pva.modules.mission.service.execution.MissionExecutionService;
import ma.org.ancfcc.pva.modules.mission.service.planification.MissionPlanificationService;

@Log4j2
@Component
@RequiredArgsConstructor
@Order(11)
public class MissionLevelTwoSeeder implements CommandLineRunner {

    @Value("${starter.database.seed}")
    private boolean seeding;

    @Value("${data.external.level_2_path}")
    private String levelTwoPath;

    private final MissionService missionService;

    private final MissionPlanificationService missionPlanificationService;
    private final MissionExecutionService missionExecutionService;

    private static final String DATE_PATTERN = "dd-MM-yyyy";
    private static final String EO_SHAPEFILE_FOLDER = "eo-shapefile";
    private static final Integer FOLDER_THREAD_POOL = 11;
    private static final Integer MISSION_THREAD_POOL = 5;

    @Override
    public void run(String... args) throws Exception {

        if (!seeding) {
            log.info("### DATA: Skipping data seeding");
            return;
        }

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
        List<String> yearFolderList = Arrays.asList("2022");

        log.info("### DATA: Début chargement données niveau 2  ...");
        processFolderList(yearFolderList);
        log.info("### DATA: Fin chargement données niveau 2 .");

    }

    private void processFolderList(List<String> folders) {
        ThreadService.executeInThreadPool(FOLDER_THREAD_POOL, folders, this::processYearFolder);
    }

    private void processYearFolder(String folder) {
        String yearFolderPath = levelTwoPath + folder;
        File resourceDir = new File(yearFolderPath);

        try {
            FileUtils.validateDirectory(resourceDir);

            File[] directories = resourceDir.listFiles(File::isDirectory);
            if (directories == null) {
                log.info("No directories found in: {}", yearFolderPath);
                return;
            }

            ThreadService.executeInThreadPool(MISSION_THREAD_POOL, Arrays.asList(directories),
                    this::processSubYearFolder);

        } catch (IOException e) {
            log.error("Error processing folder: {}", folder, e);
        }
    }

    private void processSubYearFolder(File subYearFolder) {
        String folderType = subYearFolder.getName().toLowerCase();

        if ("numerique".equals(folderType)) {
            File[] missionDirectories = subYearFolder.listFiles(File::isDirectory);
            if (missionDirectories != null) {

                processNumeriqueDirectory(missionDirectories);

                Arrays.stream(missionDirectories)
                        .filter(missionDirectory -> EO_SHAPEFILE_FOLDER.equals(missionDirectory.getName()))
                        .forEach(this::processNumeriqueEOShapefiles);
            }

        } else if ("analogique".equals(folderType)) {
            processAnalogiqueMissionFiles(subYearFolder);
        }
    }

    public void processNumeriqueDirectory(File[] missionDirectories) {
        int numThreads = 1; // Adjust the number of threads as needed
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

        Arrays.stream(missionDirectories)
                .filter(missionDirectory -> !EO_SHAPEFILE_FOLDER.equals(missionDirectory.getName()))
                .forEach(missionDirectory -> executorService
                        .submit(() -> processNumeriqueMissionFolder(missionDirectory)));

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.MINUTES)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    @Transactional
    private void processNumeriqueMissionFolder(File missionFolder) {
        String folderName = missionFolder.getName().toLowerCase();

        missionService.findByCode(
                folderName).ifPresentOrElse(
                        mission -> handleMission(mission, missionFolder),
                        () -> log.info("### DATA: mission {} not found", folderName));

    }

    private void processNumeriqueEOShapefiles(File eoShapefileFolder) {

        File[] shapefileComponents = eoShapefileFolder.listFiles();
        try {
            if (shapefileComponents.length > 0) {

                Arrays.stream(shapefileComponents)
                        .filter(File::isFile)
                        .forEach(file -> log.info("### DATA: chargement fichier EO: {}", file.getName()));

                missionExecutionService.uploadNumeriqueEOShapefile(Arrays.asList(shapefileComponents), 4326);
            }
        } catch (IOException e) {
            log.warn("### DATA: no EO shapefiles found in folder: {}", eoShapefileFolder.getName());
        }

    }

    private void handleMission(Mission mission, File missionFolder) {
        processNumeriquePLanificationMissionFiles(mission, missionFolder);

        try {
            processNumeriqueExecutionMissionFiles(missionFolder, mission);
        } catch (IOException e) {
            log.error("### DATA: Error processing execution mission files", e);
        }
    }

    private void processNumeriquePLanificationMissionFiles(Mission mission, File missionFolder) {
        Arrays.stream(missionFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".xml")))
                .forEach(file -> uploadXmlMissionPlanification(mission, file));
    }

    // planification methods
    private void processAnalogiqueMissionFiles(File yearFolder) {
        log.info("### DATA: chargement date analogique shapefile start ");
        try {
            File[] shapefileComponents = yearFolder.listFiles();
            if (shapefileComponents != null) {
                ShpFileService.validateShapefileComponents(Arrays.asList(shapefileComponents));
                uploadAnalogiqueEOShapefiles(shapefileComponents);
            }
        } catch (DateTimeParseException e) {
            log.error("Error processing analogique files in folder: {}", yearFolder.getName(), e);
        }
        log.info("### DATA:chargement date : {} done", yearFolder.getName());
    }

    private void uploadAnalogiqueEOShapefiles(File[] shapefileComponents) {
        Arrays.stream(shapefileComponents)
                .filter(File::isFile)
                .forEach(file -> log.info("### DATA: chargement fichier: {}", file.getName()));

        try {
            missionPlanificationService.uploadAnalogiqueEOShapefile(Arrays.asList(shapefileComponents), 4326);
        } catch (IOException e) {
            log.error("### DATA: Error uploading analogique shapefiles", e);
        }
    }

    private void uploadXmlMissionPlanification(Mission mission, File file) {
        try {
            log.info("### DATA: chargement planification mission:  {}", mission.getCode());
            missionPlanificationService.uploadPlanificationFile(mission, file);
        } catch (IOException | JAXBException e) {
            log.error("### DATA: Error uploading file {}: {}", file.getName(), e.getMessage());
        }
    }

    // execution methods
    private void processNumeriqueExecutionMissionFiles(File missionFolder, Mission mission) throws IOException {
        File[] missionFilesArray = missionFolder.listFiles();
        if (missionFilesArray != null) {
            for (File dateFolder : missionFilesArray) {
                if (dateFolder.isDirectory()) {
                    processExecutionDateFolder(dateFolder, mission);
                }
            }
        }
    }

    private void processExecutionDateFolder(File dateFolder, Mission mission) throws IOException {
        log.info("### DATA: chargement date : {} start", dateFolder.getName());
        try {
            LocalDate.parse(dateFolder.getName(), DateTimeFormatter.ofPattern(DATE_PATTERN));
            File[] shapefileComponents = dateFolder.listFiles(ShpFileService::isValidShapefile);

            if (shapefileComponents != null) {
                uploadExecutionShapefiles(shapefileComponents, mission.getId(), dateFolder.getName());
            }
        } catch (DateTimeParseException e) {
            log.error("Invalid date format in folder name: {}", dateFolder.getName(), e);
        }
        log.info("### DATA: chargement date : {} done", dateFolder.getName());
    }

    private void uploadExecutionShapefiles(File[] shapefileComponents, Long missionId, String dateFolder)
            throws IOException {
        Arrays.stream(shapefileComponents)
                .filter(File::isFile)
                .forEach(file -> log.info("### DATA: chargement fichier: {}", file.getName()));
        missionExecutionService.uploadNumeriqueExecutionShapefile(missionId, Arrays.asList(shapefileComponents),
                dateFolder, 4326);
    }

}
