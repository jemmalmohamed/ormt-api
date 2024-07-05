package ma.org.ancfcc.pva.modules.mission.service.planification;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.stream.Collectors;

import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityNotFoundException;
import jakarta.xml.bind.JAXBException;
import lombok.extern.log4j.Log4j2;
import ma.org.ancfcc.pva.core.commun.base.service.BaseServiceImpl;
import ma.org.ancfcc.pva.core.commun.base.service.SpecificationService;
import ma.org.ancfcc.pva.core.commun.rest.responses.MessageResponse;
import ma.org.ancfcc.pva.core.exceptions.handlers.ShapefileProcessingException;
import ma.org.ancfcc.pva.core.gis.shapefile.ShpFileService;
import ma.org.ancfcc.pva.core.gis.utils.GeometryUtils;
import ma.org.ancfcc.pva.core.utilities.FileUtils;
import ma.org.ancfcc.pva.modules.mission.bande.Bande;
import ma.org.ancfcc.pva.modules.mission.bande.service.BandeService;
import ma.org.ancfcc.pva.modules.mission.models.Mission;
import ma.org.ancfcc.pva.modules.mission.photo.execution.service.PhotoExecutionService;
import ma.org.ancfcc.pva.modules.mission.photo.orientation.service.PhotoOrientationService;
import ma.org.ancfcc.pva.modules.mission.photo.planification.PhotoPlanification;
import ma.org.ancfcc.pva.modules.mission.photo.planification.service.PhotoPlanificationService;
import ma.org.ancfcc.pva.modules.mission.repository.MissionRepository;
import ma.org.ancfcc.pva.modules.mission.service.MissionService;
import ma.org.ancfcc.pva.modules.mission.service.planification.xml.UploadXmlPlanificationService;

@Log4j2
@Service
public class MissionPlanificationServiceImpl extends BaseServiceImpl<Mission>
        implements MissionPlanificationService {

    @Value("${geoserver.workspace}")
    private String gsWorkspace;

    @Value("${geoserver.postgis-datastore}")
    private String gsPostgisDatastore;

    @Autowired
    private BandeService bandeService;

    @Autowired
    private PhotoPlanificationService photoPlanificationService;
    @Autowired
    private PhotoOrientationService photoOrientationService;
    @Autowired
    private PhotoExecutionService photoExecutionService;

    @Autowired
    UploadXmlPlanificationService uploadXmlPlanificationService;

    @Autowired
    private MissionService missionService;

    private static final String PRJ_EXT = "prj";
    private static final String SHP_EXT = "shp";
    private static final String INVALID_SHAPEFILE_MSG = "Invalid Shapefile";
    private static final String ERROR_PROCESSING_SHAPEFILE = "Error processing shapefile";
    private static final String NOT_FOUND_MSG = " not found";

    public MissionPlanificationServiceImpl(MissionRepository missionRepository,
            SpecificationService specificationService) {
        super(missionRepository, specificationService);
    }

    @Override
    public void uploadPlanificationFile(Long id, MultipartFile multipartFile) throws IOException, JAXBException {
        Mission mission = missionService.findById(id).orElseThrow(EntityNotFoundException::new);
        File xmlFile = FileUtils.convertMultipartFileToFile(multipartFile);
        uploadPlanificationFile(mission, xmlFile);
    }

    @Override
    public void uploadPlanificationFile(Long id, File file) throws IOException, JAXBException {
        Mission mission = missionService.findById(id).orElseThrow(EntityNotFoundException::new);
        uploadPlanificationFile(mission, file);
    }

    @Override
    public void uploadPlanificationFile(Mission mission, File file) throws IOException, JAXBException {
        String ext = FileUtils.getFileExtension(file);
        switch (ext) {
            case "xml":
                uploadXmlPlanificationService.uploadPlanificationXmlFile(mission, file);
                break;
            case "xls":

                break;

            default:
                break;
        }

    }

    @Override
    public void removeMissionPlanification(Long id) {
        bandeService.deleteBandesByMissionId(id);
    }

    @Override
    public void uploadAnalogiqueEOShapefile(List<File> shapefileComponents, Integer srid)
            throws IOException {
        File prjFile = FileUtils.getFileByExtFromFileListComponents(shapefileComponents, PRJ_EXT);
        File shpFile = FileUtils.getFileByExtFromFileListComponents(shapefileComponents, SHP_EXT);

        ShpFileService.validatePrjFileIsWgs(prjFile, srid);
        SimpleFeatureCollection collection = ShpFileService.getFeatureCollectionFromShapefile(shpFile);

        processAnalogiqueCollection(collection, srid);

    }

    private void processAnalogiqueCollection(SimpleFeatureCollection collection, Integer srid) {
        try {
            Map<String, List<SimpleFeature>> groupedFeatures = new HashMap<>();

            try (SimpleFeatureIterator iterator = collection.features()) {
                while (iterator.hasNext()) {
                    SimpleFeature feature = iterator.next();
                    String missionField = (String) feature.getAttribute("Mission");

                    groupedFeatures.computeIfAbsent(missionField, k -> new ArrayList<>()).add(feature);
                }
            }
            processMissionAnalogiqueFeatures(groupedFeatures, srid);

        } catch (Exception e) {
            log.error("Error while creating execution from shapefile: {}", e.getMessage(), e);
            throw new ShapefileProcessingException(buildErrorResponse(e));
        }
    }

    private void processMissionAnalogiqueFeatures(Map<String, List<SimpleFeature>> groupedFeatures, Integer srid) {
        for (Map.Entry<String, List<SimpleFeature>> entry : groupedFeatures.entrySet()) {
            String missionCode = entry.getKey();
            Mission mission = missionService.findByCode(missionCode)
                    .orElseThrow(
                            () -> new EntityNotFoundException("Mission with code: " + missionCode + NOT_FOUND_MSG));

            Map<String, List<SimpleFeature>> bandeGroups = entry.getValue().stream()
                    .collect(Collectors.groupingBy(feature -> (String) feature.getAttribute("Bande")));

            for (Map.Entry<String, List<SimpleFeature>> bandePhotosEntry : bandeGroups.entrySet()) {
                List<SimpleFeature> bandePhotosFeatures = bandePhotosEntry.getValue();

                Point startPoint = null;
                Point endPoint = null;
                for (SimpleFeature featurePhoto : bandePhotosFeatures) {
                    String photoType = (String) featurePhoto.getAttribute("Photo_Type");
                    Point centerPoint = GeometryUtils.extract2DPointFromFeature(featurePhoto);

                    if ("start".equals(photoType)) {
                        startPoint = centerPoint;
                    } else if ("end".equals(photoType)) {
                        endPoint = centerPoint;
                    }
                }

                if (startPoint != null && endPoint != null) {
                    Bande bande = saveBande(bandePhotosEntry.getKey(), startPoint, endPoint, mission, srid);
                    savePhotoPlanificationsFromSimpleFeature(bandePhotosFeatures, bande, srid);
                }
            }
        }
    }

    private void savePhotoPlanificationsFromSimpleFeature(List<SimpleFeature> bandePhotosFeatures, Bande bande,
            Integer srid) {
        for (SimpleFeature featurePhoto : bandePhotosFeatures) {
            PhotoPlanification photoPlanification = savePhotoPlanification(featurePhoto, bande, srid);
            photoOrientationService.savePhotoOrientationFromShpFeature(photoPlanification, featurePhoto, srid);
            photoExecutionService.savePhotoExecution(photoPlanification, featurePhoto);
        }
    }

    private PhotoPlanification savePhotoPlanification(SimpleFeature featurePhoto, Bande bande, Integer srid) {
        Point centre = GeometryUtils.extract2DPointFromFeature(featurePhoto, srid);
        String photoName = featurePhoto.getAttribute("Photo").toString();
        PhotoPlanification photoPlanification = new PhotoPlanification();
        photoPlanification.setBande(bande);
        photoPlanification.setNom(photoName);
        photoPlanification.setLabel(photoName);
        photoPlanification.setCentre(centre);
        return photoPlanificationService.create(photoPlanification);

    }

    private Bande saveBande(String name, Point startPoint, Point endPoint, Mission mission, Integer srid) {
        return bandeService.saveBandePlanificationFromShapeFileFeature(name, startPoint, endPoint, mission, srid);
    }

    private String buildErrorResponse(Exception e) {
        return MessageResponse.builder()
                .title(INVALID_SHAPEFILE_MSG)
                .mainMessage(ERROR_PROCESSING_SHAPEFILE + ": " + e.getMessage())
                .subMessage("Please check the mission execution attributes")
                .build()
                .format();
    }

}