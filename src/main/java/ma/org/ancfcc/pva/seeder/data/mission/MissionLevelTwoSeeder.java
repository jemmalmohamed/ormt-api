package ma.org.ancfcc.pva.seeder.data.mission;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.xml.bind.JAXBException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ma.org.ancfcc.pva.core.gis.shapefile.ShpFileService;
import ma.org.ancfcc.pva.core.threads.ThreadService;
import ma.org.ancfcc.pva.core.utilities.FileUtils;
import ma.org.ancfcc.pva.modules.mission.models.Mission;
import ma.org.ancfcc.pva.modules.mission.service.MissionService;
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

    @Override
    public void run(String... args) throws Exception {

        if (!seeding) {
            log.info("### DATA: Skipping data seeding");
            return;
        }
        List<String> yearFolderList = Arrays.asList(
                "2013",
                "2014",
                "2015",
                "2016",
                "2017",
                "2018",
                "2019",
                "2020",
                "2021",
                "2022",
                "2023",
                "2024");
        // List<String> yearFolderList = Arrays.asList("2015");

        log.info("### DATA: Début chargement données d'execution format SHP...");
        processFolderList(yearFolderList);
        log.info("### DATA: Fin chargement données d'execution format SHP.");

    }

    private void processFolderList(List<String> folders) {
        ThreadService.executeInThreadPool(1, folders, this::processYearFolder);
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

            ThreadService.executeInThreadPool(1, Arrays.asList(directories), this::processSubYearFolder);

        } catch (IOException e) {
            log.error("Error processing folder: {}", folder, e);
        }
    }

    private void processSubYearFolder(File subYearFolder) {
        String folderType = subYearFolder.getName().toLowerCase();

        if ("numerique".equals(folderType)) {
            File[] missionDirectories = subYearFolder.listFiles(File::isDirectory);
            if (missionDirectories != null) {
                Arrays.stream(missionDirectories).forEach(this::processNumeriqueFolder);
            }
        } else if ("analogique".equals(folderType)) {
            processAnalogiqueMissionFiles(subYearFolder);
        }
    }

    private void processNumeriqueFolder(File missionFolder) {
        String codeMission = missionFolder.getName().toLowerCase();
        missionService.findByCode(codeMission).ifPresentOrElse(
                mission -> processNumeriqueMissionFiles(mission, missionFolder),
                () -> log.info("### DATA: mission {} not found", codeMission));
    }

    private void processNumeriqueMissionFiles(Mission mission, File missionFolder) {
        Arrays.stream(missionFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".xml")))
                .forEach(file -> uploadMissionPlanification(mission, file));
    }

    private void processAnalogiqueMissionFiles(File yearFolder) {
        log.info("### DATA: chargement date analogique shapefile start ");
        try {
            File[] shapefileComponents = yearFolder.listFiles();
            if (shapefileComponents != null) {
                ShpFileService.validateShapefileComponents(Arrays.asList(shapefileComponents));
                uploadAnalogiqueShapefiles(shapefileComponents);
            }
        } catch (DateTimeParseException e) {
            log.error("Error processing analogique files in folder: {}", yearFolder.getName(), e);
        }
        log.info("### DATA: Chargement date : {} done", yearFolder.getName());
    }

    private void uploadAnalogiqueShapefiles(File[] shapefileComponents) {
        Arrays.stream(shapefileComponents)
                .filter(File::isFile)
                .forEach(file -> log.info("### DATA: chargement fichier: {}", file.getName()));

        try {
            missionPlanificationService.uploadAnalogiqueShapefile(Arrays.asList(shapefileComponents), 4326);
        } catch (IOException e) {
            log.error("### DATA: Error uploading analogique shapefiles", e);
        }
    }

    private void uploadMissionPlanification(Mission mission, File file) {
        try {
            log.info("### DATA: Chargement planification mission:  {}", mission.getCode());
            missionPlanificationService.uploadPlanificationFile(mission, file);
        } catch (IOException | JAXBException e) {
            log.error("### DATA: Error uploading file {}: {}", file.getName(), e.getMessage());
        }
    }

}
