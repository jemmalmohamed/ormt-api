package ma.org.ormt.modules.mission.service.execution;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import java.util.function.Consumer;

import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import ma.org.ormt.core.commun.base.service.BaseServiceImpl;
import ma.org.ormt.core.commun.base.service.SpecificationService;
import ma.org.ormt.core.commun.rest.responses.MessageResponse;
import ma.org.ormt.core.exceptions.handlers.ShapefileProcessingException;
import ma.org.ormt.core.gis.shapefile.ShpFileService;
import ma.org.ormt.core.gis.shapefile.ShpSimpleFeatureService;
import ma.org.ormt.core.gis.utils.GeometryConversion;
import ma.org.ormt.core.gis.utils.GeometryUtils;
import ma.org.ormt.core.threads.ThreadService;
import ma.org.ormt.core.utilities.DateUtils;
import ma.org.ormt.core.utilities.FileUtils;
import ma.org.ormt.modules.mission.bande.Bande;
import ma.org.ormt.modules.mission.bande.service.BandeService;
import ma.org.ormt.modules.mission.models.Mission;
import ma.org.ormt.modules.mission.photo.execution.PhotoExecution;
import ma.org.ormt.modules.mission.photo.execution.service.PhotoExecutionService;
import ma.org.ormt.modules.mission.photo.orientation.service.PhotoOrientationService;
import ma.org.ormt.modules.mission.photo.planification.PhotoPlanification;
import ma.org.ormt.modules.mission.photo.planification.service.PhotoPlanificationService;
import ma.org.ormt.modules.mission.repository.MissionRepository;
import ma.org.ormt.modules.mission.scan.ScanExecution;
import ma.org.ormt.modules.mission.scan.service.ScanExecutionService;
import ma.org.ormt.modules.mission.service.MissionService;
import ma.org.ormt.modules.mission.service.planification.helper.MissionPlanificationHelper;

@Log4j2
@Service
public class MissionExecutionServiceImpl extends BaseServiceImpl<Mission>
        implements MissionExecutionService {

    @Value("${geoserver.workspace}")
    private String gsWorkspace;

    @Value("${geoserver.postgis-datastore}")
    private String gsPostgisDatastore;

    @Autowired
    private BandeService bandeService;

    @Autowired
    private ScanExecutionService scanExecutionService;
    @Autowired
    private PhotoPlanificationService photoPlanificationService;

    @Autowired
    private PhotoExecutionService photoExecutionService;

    @Autowired
    private PhotoOrientationService photoOrientationService;

    @Autowired
    private MissionPlanificationHelper missionPlanificationHelper;

    @Autowired
    private MissionService missionService;

    private static final String PRJ_EXT = "prj";
    private static final String SHP_EXT = "shp";
    private static final String INVALID_SHAPEFILE_MSG = "Invalid Shapefile";
    private static final String ERROR_PROCESSING_SHAPEFILE = "Error processing shapefile";
    private static final String NOT_FOUND_MSG = " not found";

    public MissionExecutionServiceImpl(MissionRepository missionRepository,
            SpecificationService specificationService) {
        super(missionRepository, specificationService);
    }

    @Override
    public void uploadNumeriqueExecutionShapefile(Long missionId, List<File> shapefileComponents, String dateFolder,
            Integer srid) throws IOException {
        Mission mission = missionService.findById(missionId).orElseThrow(EntityNotFoundException::new);
        processNumeriqueShapefiles(mission, shapefileComponents, dateFolder, srid);
    }

    private void processNumeriqueShapefiles(Mission mission, List<File> shapefileComponents, String dateFolder,
            Integer srid)
            throws IOException {
        File prjFile = FileUtils.getFileByExtFromFileListComponents(shapefileComponents, PRJ_EXT);
        File shpFile = FileUtils.getFileByExtFromFileListComponents(shapefileComponents, SHP_EXT);

        ShpFileService.validatePrjFileIsWgs(prjFile, srid);

        SimpleFeatureCollection collection = ShpFileService.getFeatureCollectionFromShapefile(shpFile);

        try (SimpleFeatureIterator iterator = collection.features()) {
            while (iterator.hasNext()) {
                processNumeriqueFeature(iterator.next(), mission, dateFolder, srid);
            }
        } catch (Exception e) {
            log.error("Error processing shapefiles for mission {}: {}", mission.getId(), e.getMessage(), e);
        }
    }

    private void processNumeriqueFeature(SimpleFeature feature, Mission mission, String dateFolder, Integer srid) {
        try {
            String sensorName = mission.getCapteur().getCode();
            switch (sensorName) {
                case "ADS40/80":
                    scanExecutionService
                            .create(createScanExecutionFromShpFeature(feature, mission, dateFolder, srid));
                    break;
                case "DMC_II_230":
                    photoExecutionService
                            .create(createPhotoExecutionFromShpFeature(feature, mission, dateFolder, srid));

                    break;
                default:
                    log.info("No processing required for sensor: {}", sensorName);
            }
        } catch (Exception e) {
            log.error("Error while creating execution from shapefile: {}", e.getMessage(), e);
            throw new ShapefileProcessingException(buildErrorResponse(e));
        }
    }

    private ScanExecution createScanExecutionFromShpFeature(SimpleFeature feature, Mission mission,
            String dateFolder,
            Integer srid) {

        String bandeLabel = ShpSimpleFeatureService.getValueFromFeature(feature, "Line");

        Bande bande = getBandeByLabelAndMission(bandeLabel, mission);

        LocalDate date = DateUtils.parseLocalDate(dateFolder, "dd-MM-yyyy");

        ScanExecution scanExecution = new ScanExecution();

        if (feature.getDefaultGeometry() instanceof Geometry) {
            Geometry geometry = (Geometry) feature.getDefaultGeometry();
            geometry = GeometryUtils.geometryIsPolygonOrMultiPolygon(geometry);
            geometry = GeometryConversion.convertTo2D(geometry);

            MultiPolygon multiPolygon = (MultiPolygon) geometry;

            if (multiPolygon.getNumGeometries() > 0) {
                Polygon polygon = (Polygon) multiPolygon.getGeometryN(0); // Extract the first polygon
                polygon.setSRID(srid);
                scanExecution.setNom(bandeLabel);
                scanExecution.setLabel(bandeLabel);
                scanExecution.setDatePva(date);
                scanExecution.setEmprise(polygon);
                scanExecution.setBande(bande);
            }
        }
        return scanExecution;
    }

    private PhotoExecution createPhotoExecutionFromShpFeature(SimpleFeature feature, Mission mission, String dateFolder,
            Integer srid) {

        String bandeLabel = ShpSimpleFeatureService.getValueFromFeature(feature, "Line");
        String photoPLanificationName = ShpSimpleFeatureService.getValueFromFeature(feature, "Event");

        PhotoPlanification photoPlanification = getPhotoPlanificationByBandeAndMission(photoPLanificationName,
                bandeLabel, mission);

        LocalDate date = DateUtils.parseLocalDate(dateFolder, "dd-MM-yyyy");
        PhotoExecution photoExecution = new PhotoExecution();
        if (feature.getDefaultGeometry() instanceof Geometry) {
            Geometry geometry = (Geometry) feature.getDefaultGeometry();
            geometry = GeometryUtils.geometryIsPolygonOrMultiPolygon(geometry);
            geometry = GeometryConversion.convertTo2D(geometry);
            MultiPolygon multiPolygon = (MultiPolygon) geometry;
            if (multiPolygon.getNumGeometries() > 0) {
                Polygon polygon = (Polygon) multiPolygon.getGeometryN(0); // Extract the first polygon
                polygon.setSRID(srid);
                photoExecution.setEmprise(polygon);
                photoExecution.setDatePva(date);
                photoExecution.setPhotoPlanification(photoPlanification);

            }
        }
        return photoExecution;
    }

    private Bande getBandeByLabelAndMission(String bandeLabel, Mission mission) {
        return bandeService.findBandeByLabelAndMissionId(bandeLabel, mission.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Bande with label: " + bandeLabel + " and mission id: " + mission.getId() + NOT_FOUND_MSG));

    }

    private PhotoPlanification getPhotoPlanificationByBandeAndMission(String photoPlanificationLabel, String bandeLabel,
            Mission mission) {
        Bande bande = getBandeByLabelAndMission(bandeLabel, mission);
        return photoPlanificationService.findPhotoPlanificationByLabelAndBandeId(photoPlanificationLabel, bande.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "PhotoPlanification with label: " + photoPlanificationLabel + " and bande id: " + bande.getId()
                                + NOT_FOUND_MSG));
    }

    private String buildErrorResponse(Exception e) {
        return MessageResponse.builder()
                .title(INVALID_SHAPEFILE_MSG)
                .mainMessage(ERROR_PROCESSING_SHAPEFILE + ": " + e.getMessage())
                .subMessage("Please check the mission execution attributes")
                .build()
                .format();
    }

    @Override
    public void uploadNumeriqueEOShapefile(List<File> shapefileComponents, Integer srid) throws IOException {
        File prjFile = FileUtils.getFileByExtFromFileListComponents(shapefileComponents, PRJ_EXT);
        File shpFile = FileUtils.getFileByExtFromFileListComponents(shapefileComponents, SHP_EXT);

        try {

            ShpFileService.validatePrjFileIsWgs(prjFile, srid);
            SimpleFeatureCollection collection = ShpFileService.getFeatureCollectionFromShapefile(shpFile);
            processEoCollection(collection, srid);
        } catch (Exception e) {
            log.error("Error while validating prj file: {}", e.getMessage(), e);
        }

    }

    private void processEoCollection(SimpleFeatureCollection collection, Integer srid) {

        List<SimpleFeature> features = new ArrayList<>();

        try (SimpleFeatureIterator iterator = collection.features()) {
            while (iterator.hasNext()) {
                features.add(iterator.next());
            }
        } catch (Exception e) {
            log.error("Error while creating execution from shapefile: {}", e.getMessage(), e);
            throw new ShapefileProcessingException(buildErrorResponse(e));
        }

        Consumer<SimpleFeature> featureProcessor = feature -> processMissionEOFeature(feature, srid);
        ThreadService.executeInThreadPool(10, features, featureProcessor);

    }

    public void processMissionEOFeature(SimpleFeature feature, Integer srid) {
        String missionCode = ShpSimpleFeatureService.getValueFromFeature(feature, "Mission");
        String bandeNom = ShpSimpleFeatureService.getValueFromFeature(feature, "Bande");
        String photoNom = ShpSimpleFeatureService.getValueFromFeature(feature, "Photo");

        String bandeNomFormatted = missionPlanificationHelper.formaLabel(bandeNom);

        PhotoPlanification photoPlanification = photoPlanificationService
                .findPhotoPlanificationByNomAndBandeNomAndMissionCode(
                        photoNom,
                        bandeNomFormatted, missionCode)
                .orElseThrow(() -> new EntityNotFoundException("PhotoPlanification with nom: " + photoNom
                        + " and bande nom: " + bandeNomFormatted + " and mission code: " + missionCode
                        + NOT_FOUND_MSG));

        photoOrientationService
                .savePhotoOrientationFromShpFeature(photoPlanification, feature, srid);

    }

}